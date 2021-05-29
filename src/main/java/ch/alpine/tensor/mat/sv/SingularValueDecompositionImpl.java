// code adapted by jph
package ch.alpine.tensor.mat.sv;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.nrm.Hypot;
import ch.alpine.tensor.nrm.Matrix1Norm;
import ch.alpine.tensor.nrm.Vector1Norm;
import ch.alpine.tensor.nrm.Vector2NormSquared;
import ch.alpine.tensor.red.CopySign;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.Sqrt;

/* package */ class SingularValueDecompositionImpl implements SingularValueDecomposition, Serializable {
  /** Difference between 1.0 and the minimum double greater than 1.0
   * DBL_EPSILON == 2.220446049250313E-16 */
  private static final Scalar DBL_EPSILON = DoubleScalar.of(Math.nextUp(1.0) - 1.0);
  private static final int MAX_ITERATIONS = 28;
  // ---
  private final int rows;
  private final int cols;
  /** rows x cols */
  private final Tensor u;
  private final Tensor w;
  private final Tensor r;
  /** cols x cols */
  private final Tensor v;

  /** @param matrix with cols <= rows */
  public SingularValueDecompositionImpl(Tensor matrix) {
    rows = matrix.length();
    cols = Unprotect.dimension1(matrix);
    if (rows < cols)
      throw new IllegalArgumentException("rows=" + rows + " cols=" + cols);
    u = matrix.copy();
    w = Array.zeros(cols);
    r = Array.zeros(cols);
    // ---
    for (int i = 0; i < cols; ++i) {
      initU1(i);
      initU2(i);
    }
    Chop chop = Chop.below(Matrix1Norm.of(Tensors.of(w, r).map(Unprotect::withoutUnit)) //
        .multiply(DBL_EPSILON) //
        .number().doubleValue());
    // ---
    v = Array.zeros(cols, cols);
    v.set(RealScalar.ONE, cols - 1, cols - 1);
    for (int i = cols - 2; 0 <= i; --i)
      initV(i);
    for (int i = cols - 1; 0 <= i; --i)
      initU3(i);
    for (int i = cols - 1; 0 <= i; --i) {
      for (int iteration = 0; iteration <= MAX_ITERATIONS; ++iteration) {
        int l = levelW(i, chop);
        if (l == i)
          break;
        if (iteration == MAX_ITERATIONS)
          throw new RuntimeException("no convergence");
        rotateUV(l, i);
      }
      if (Sign.isNegative(w.Get(i))) { // ensure w[i] is positive
        w.set(Scalar::negate, i);
        v.set(Scalar::negate, Tensor.ALL, i);
      }
    }
  }

  @Override // from SingularValueDecomposition
  public Tensor getU() {
    return u;
  }

  @Override // from SingularValueDecomposition
  public Tensor values() {
    return w;
  }

  @Override // from SingularValueDecomposition
  public Tensor getV() {
    return v;
  }

  private void initU1(int i) {
    Scalar scale = Vector1Norm.of(u.stream().skip(i).map(row -> row.Get(i)));
    if (Scalars.isZero(scale)) {
      Scalar zero_unitless = scale.one().zero();
      u.stream().skip(i).forEach(uk -> uk.set(zero_unitless, i));
      w.set(scale.zero(), i);
    } else {
      u.stream().skip(i).forEach(uk -> uk.set(scale::under, i));
      Scalar s = Vector2NormSquared.of(u.stream().skip(i).map(row -> row.Get(i)));
      Scalar f = u.Get(i, i);
      Scalar p = CopySign.of(Sqrt.FUNCTION.apply(s), f).negate();
      Scalar h = f.multiply(p).subtract(s);
      u.set(f.subtract(p), i, i);
      for (int j = i + 1; j < cols; ++j) {
        final int fj = j;
        Scalar dot = u.stream().skip(i) //
            .map(row -> row.Get(i).multiply(row.Get(fj))) //
            .reduce(Scalar::add).orElseThrow();
        addScaled(i, u, i, j, dot.divide(h));
      }
      u.stream().skip(i).forEach(uk -> uk.set(scale::multiply, i));
      w.set(scale.multiply(p), i);
    }
  }

  private void initU2(int i) {
    final int ip1 = i + 1;
    if (ip1 != cols) {
      Scalar scale = Vector1Norm.of(u.get(i).extract(ip1, cols));
      if (Scalars.isZero(scale)) {
        Scalar zero_unitless = scale.one().zero();
        IntStream.range(ip1, cols).forEach(k -> u.set(zero_unitless, i, k));
        r.set(scale.zero(), ip1);
      } else {
        IntStream.range(ip1, cols).forEach(k -> u.set(scale::under, i, k));
        Scalar s = Vector2NormSquared.of(u.get(i).extract(ip1, cols));
        Scalar f = u.Get(i, ip1);
        Scalar p = CopySign.of(Sqrt.FUNCTION.apply(s), f).negate();
        Scalar h = f.multiply(p).subtract(s);
        u.set(f.subtract(p), i, ip1);
        IntStream.range(ip1, cols).forEach(k -> r.set(u.Get(i, k).divide(h), k));
        Tensor ui = u.get(i).extract(ip1, cols);
        u.stream().skip(ip1).forEach(uj -> {
          Scalar d = (Scalar) uj.extract(ip1, cols).dot(ui);
          for (int k = ip1; k < cols; ++k)
            uj.set(d.multiply(r.Get(k))::add, k);
        });
        IntStream.range(ip1, cols).forEach(k -> u.set(scale::multiply, i, k));
        r.set(scale.multiply(p), ip1);
      }
    }
  }

  private void initV(int i) {
    final int ip1 = i + 1;
    Scalar p = r.Get(ip1);
    if (Scalars.nonZero(p)) {
      Tensor ui = u.get(i);
      Scalar ui_ip1 = ui.Get(ip1).multiply(p);
      AtomicInteger aj = new AtomicInteger(ip1);
      v.stream().skip(ip1).forEach(vj -> vj.set(ui.Get(aj.getAndIncrement()).divide(ui_ip1), i));
      Tensor uiEx = ui.extract(ip1, cols);
      for (int j = ip1; j < cols; ++j) {
        final int fj = j;
        addScaled(ip1, v, i, j, //
            (Scalar) uiEx.dot(Tensor.of(v.stream().skip(ip1).map(row -> row.Get(fj)))));
      }
    }
    IntStream.range(ip1, cols).forEach(j -> v.set(Scalar::zero, i, j));
    v.stream().skip(ip1).forEach(vj -> vj.set(Scalar::zero, i));
    v.set(Scalar::one, i, i);
  }

  private void initU3(int i) {
    final int ip1 = i + 1;
    Scalar p = w.Get(i);
    Scalar zero_unitless = p.one().zero();
    IntStream.range(ip1, cols).forEach(j -> u.set(zero_unitless, i, j));
    if (Scalars.isZero(p))
      u.stream().skip(i).forEach(uj -> uj.set(zero_unitless, i));
    else {
      Scalar den = u.Get(i, i).multiply(p);
      for (int j = ip1; j < cols; ++j) {
        final int fj = j;
        Scalar s = u.stream() //
            .skip(ip1) // ip1 until rows
            .map(row -> row.Get(i).multiply(row.Get(fj))) //
            .reduce(Scalar::add).orElseThrow();
        addScaled(i, u, i, j, s.divide(den));
      }
      u.stream().skip(i).forEach(uj -> uj.set(p::under, i));
    }
    u.set(RealScalar.ONE::add, i, i);
  }

  private int levelW(int k, Chop chop) {
    for (int l = k; l > 0; --l) {
      Scalar rl = r.Get(l);
      if (chop.isZero(rl))
        return l;
      if (chop.isZero(w.Get(l - 1))) {
        Scalar c = rl.zero();
        Scalar s = rl.one();
        for (int i = l; i < k + 1; ++i) {
          Scalar f = s.multiply(r.Get(i));
          r.set(c::multiply, i);
          if (chop.isZero(f))
            break;
          Scalar g = w.Get(i);
          Scalar h = Hypot.of(f, g);
          w.set(h, i);
          c = g.divide(h);
          s = f.divide(h).negate();
          rotate(u, c, s, i, l - 1);
        }
        return l;
      }
    }
    return 0;
  }

  /** @param l < i
   * @param i > 0 */
  private void rotateUV(int l, int i) {
    Scalar x = w.Get(l);
    Scalar y = w.Get(i - 1);
    Scalar z = w.Get(i);
    Scalar p = r.Get(i - 1);
    Scalar h = r.Get(i);
    Scalar hy = h.multiply(y);
    // ((y-z)(y+z)+(p-h)*(p+h))/(2hy)
    Scalar f = y.subtract(z).multiply(y.add(z)).add(p.subtract(h).multiply(p.add(h))).divide(hy.add(hy));
    p = Hypot.withOne(f);
    // ((x-z)(x+z)+(h*(y/(f+-p)-h)))/x
    f = x.subtract(z).multiply(x.add(z)).add(h.multiply(y.divide(f.add(CopySign.of(p, f))).subtract(h))).divide(x);
    Scalar s = x.one();
    Scalar c = x.one();
    for (int j = l; j < i; ++j) {
      int jp1 = j + 1;
      p = r.Get(jp1);
      y = w.Get(jp1);
      h = s.multiply(p);
      p = c.multiply(p);
      z = Hypot.of(f, h);
      r.set(z, j);
      c = f.divide(z);
      s = h.divide(z);
      rotate(v, c, s, jp1, j);
      Rotate rotate = new Rotate(p, x, c, s);
      p = rotate.re();
      f = rotate.im();
      h = y.multiply(s);
      y = y.multiply(c);
      z = Hypot.of(f, h);
      w.set(z, j);
      if (Scalars.nonZero(z)) {
        c = f.divide(z);
        s = h.divide(z);
      }
      rotate(u, c, s, jp1, j);
      rotate = new Rotate(y, p, c, s);
      x = rotate.re();
      f = rotate.im();
    }
    r.set(Scalar::zero, l);
    r.set(f, i);
    w.set(x, i);
  }

  private static void addScaled(int l, Tensor v, int i, int j, Scalar s) {
    v.stream().skip(l).forEach(vk -> vk.set(s.multiply(vk.Get(i))::add, j));
  }

  private static void rotate(Tensor m, Scalar c, Scalar s, int i, int j) {
    m.stream().forEach(mk -> {
      Rotate rotate = new Rotate(mk.Get(i), mk.Get(j), c, s);
      mk.set(rotate.re(), i);
      mk.set(rotate.im(), j);
    });
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s]", //
        SingularValueDecomposition.class.getSimpleName(), //
        Tensors.message(getU(), values(), getV()));
  }
}
