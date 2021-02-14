// code adapted by jph
package ch.ethz.idsc.tensor.mat;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.nrm.Hypot;
import ch.ethz.idsc.tensor.nrm.MatrixNorm1;
import ch.ethz.idsc.tensor.nrm.VectorNorm1;
import ch.ethz.idsc.tensor.nrm.VectorNorm2Squared;
import ch.ethz.idsc.tensor.red.CopySign;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.sca.Sqrt;

/* package */ class SingularValueDecompositionImpl implements SingularValueDecomposition, Serializable {
  private static final long serialVersionUID = 3776018097501894626L;
  private static final Scalar _0 = DoubleScalar.of(0);
  private static final Scalar _1 = DoubleScalar.of(1);
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
    Chop chop = Chop.below(MatrixNorm1.of(Tensors.of(w, r).map(Unprotect::withoutUnit)) //
        .multiply(DBL_EPSILON) //
        .number().doubleValue());
    // ---
    v = Array.zeros(cols, cols);
    v.set(_1, cols - 1, cols - 1);
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
      positiveW(i);
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
    Scalar p = _0;
    Scalar scale = VectorNorm1.of(u.stream().skip(i).map(row -> row.Get(i)));
    if (Scalars.nonZero(scale)) {
      u.stream().skip(i).forEach(uk -> uk.set(scale::under, i));
      Scalar s = VectorNorm2Squared.of(u.stream().skip(i).map(row -> row.Get(i)));
      Scalar f = u.Get(i, i);
      p = CopySign.of(Sqrt.FUNCTION.apply(s), f).negate();
      Scalar h = f.multiply(p).subtract(s);
      u.set(f.subtract(p), i, i);
      for (int j = i + 1; j < cols; ++j) {
        final int fj = j;
        Scalar dot = u.stream().skip(i) //
            .map(row -> row.Get(i).multiply(row.Get(fj))) //
            .reduce(Scalar::add).get();
        addScaled(i, u, i, j, dot.divide(h));
      }
      u.stream().skip(i).forEach(uk -> uk.set(scale::multiply, i));
    }
    w.set(scale.multiply(p), i);
  }

  private void initU2(int i) {
    final int ip1 = i + 1;
    if (ip1 != cols) {
      Scalar p = _0;
      Scalar scale = VectorNorm1.of(u.get(i).extract(ip1, cols));
      if (Scalars.nonZero(scale)) {
        IntStream.range(ip1, cols).forEach(k -> u.set(scale::under, i, k));
        {
          Scalar s = VectorNorm2Squared.of(u.get(i).extract(ip1, cols));
          Scalar f = u.Get(i, ip1);
          p = CopySign.of(Sqrt.FUNCTION.apply(s), f).negate();
          Scalar h = f.multiply(p).subtract(s);
          u.set(f.subtract(p), i, ip1);
          IntStream.range(ip1, cols).forEach(k -> r.set(u.Get(i, k).divide(h), k));
        }
        Tensor ui = u.get(i).extract(ip1, cols);
        u.stream().skip(ip1).forEach(uj -> {
          Scalar s = (Scalar) uj.extract(ip1, cols).dot(ui);
          for (int k = ip1; k < cols; ++k)
            uj.set(s.multiply(r.Get(k))::add, k);
        });
        IntStream.range(ip1, cols).forEach(k -> u.set(scale::multiply, i, k));
      }
      r.set(scale.multiply(p), ip1);
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
    IntStream.range(ip1, cols).forEach(j -> v.set(_0, i, j));
    v.stream().skip(ip1).forEach(vj -> vj.set(_0, i));
    v.set(_1, i, i);
  }

  private void initU3(int i) {
    final int ip1 = i + 1;
    IntStream.range(ip1, cols).forEach(j -> u.set(_0, i, j));
    Scalar p = w.Get(i);
    if (Scalars.isZero(p))
      u.stream().skip(i).forEach(uj -> uj.set(_0, i));
    else {
      Scalar den = u.Get(i, i).multiply(p);
      for (int j = ip1; j < cols; ++j) {
        final int fj = j;
        Scalar s = u.stream() //
            .skip(ip1) // ip1 until rows
            .map(row -> row.Get(i).multiply(row.Get(fj))) //
            .reduce(Scalar::add).get();
        addScaled(i, u, i, j, s.divide(den));
      }
      u.stream().skip(i).forEach(uj -> uj.set(p::under, i));
    }
    u.set(_1::add, i, i);
  }

  private int levelW(int k, Chop chop) {
    for (int l = k; l > 0; --l) {
      if (chop.isZero(r.Get(l)))
        return l;
      if (chop.isZero(w.Get(l - 1))) {
        Scalar c = _0;
        Scalar s = _1;
        for (int i = l; i < k + 1; ++i) {
          Scalar f = s.multiply(r.Get(i));
          r.set(c.multiply(r.Get(i)), i);
          if (chop.isZero(f)) // only sometimes covered in tests
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
    Scalar f = y.subtract(z).multiply(y.add(z)).add(p.subtract(h).multiply(p.add(h))).divide(hy.add(hy));
    p = Hypot.of(f, _1);
    f = x.subtract(z).multiply(x.add(z)).add(h.multiply(y.divide(f.add(CopySign.of(p, f))).subtract(h))).divide(x);
    Scalar s = _1;
    Scalar c = _1;
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
      f = x.multiply(c).add(p.multiply(s));
      p = p.multiply(c).subtract(x.multiply(s));
      h = y.multiply(s);
      y = y.multiply(c);
      z = Hypot.of(f, h);
      w.set(z, j);
      if (Scalars.nonZero(z)) { // <- never false in tests?
        c = f.divide(z);
        s = h.divide(z);
      }
      rotate(u, c, s, jp1, j);
      f = c.multiply(p).add(s.multiply(y));
      x = c.multiply(y).subtract(s.multiply(p));
    }
    r.set(_0, l);
    r.set(f, i);
    w.set(x, i);
  }

  private void positiveW(int i) {
    Scalar z = w.Get(i);
    if (Sign.isNegative(z)) {
      w.set(z.negate(), i);
      v.set(Scalar::negate, Tensor.ALL, i);
    }
  }

  private static void addScaled(int l, Tensor v, int i, int j, Scalar s) {
    v.stream().skip(l).forEach(vk -> vk.set(s.multiply(vk.Get(i))::add, j));
  }

  private static void rotate(Tensor m, Scalar c, Scalar s, int i, int j) {
    m.stream().forEach(mk -> {
      Scalar x = mk.Get(j);
      Scalar z = mk.Get(i);
      mk.set(x.multiply(c).add(z.multiply(s)), j);
      mk.set(z.multiply(c).subtract(x.multiply(s)), i);
    });
  }
}
