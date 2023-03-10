// code adapted by jph
package ch.alpine.tensor.mat.sv;

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
import ch.alpine.tensor.nrm.Matrix1Norm;
import ch.alpine.tensor.nrm.Vector1Norm;
import ch.alpine.tensor.nrm.Vector2NormSquared;
import ch.alpine.tensor.red.CopySign;
import ch.alpine.tensor.red.EqualsReduce;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.pow.Sqrt;

/* package */ class Init {
  /** Difference between 1.0 and the minimum double greater than 1.0
   * DBL_EPSILON == 2.220446049250313E-16 */
  private static final Scalar DBL_EPSILON = DoubleScalar.of(Math.nextUp(1.0) - 1.0);
  // ---
  private final int cols;
  /** rows x cols */
  final Tensor u;
  final Tensor w;
  final Tensor r;
  /** cols x cols */
  final Tensor v;
  final Chop chop;

  /** @param matrix with cols <= rows
   * @throws Exception if rows < cols
   * @throws Exception if matrix does not have entries with unique unit */
  public Init(Tensor matrix) {
    cols = Unprotect.dimension1(matrix);
    if (matrix.length() < cols)
      throw new IllegalArgumentException("rows=" + matrix.length() + " cols=" + cols);
    // ---
    u = matrix.copy();
    w = Array.same(EqualsReduce.zero(matrix), cols);
    r = w.copy();
    // ---
    for (int i = 0; i < cols; ++i) {
      initU1(i);
      initU2(i);
    }
    // the computation of the matrix norm ensures that the unit of the entries in w and r is unique
    chop = Chop.below(Unprotect.withoutUnit(Matrix1Norm.of(Tensors.of(w, r)) //
        .multiply(DBL_EPSILON)).number().doubleValue());
    // ---
    v = Array.zeros(cols, cols);
    v.set(RealScalar.ONE, cols - 1, cols - 1);
    for (int i = cols - 2; 0 <= i; --i)
      initV(i);
    for (int i = cols - 1; 0 <= i; --i)
      initU3(i);
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
      {
        AtomicInteger aj = new AtomicInteger(ip1);
        v.stream().skip(ip1).forEach(vj -> vj.set(ui.Get(aj.getAndIncrement()).divide(ui_ip1), i));
      }
      Tensor uiEx = ui.extract(ip1, cols);
      IntStream.range(ip1, cols) //
          .forEach(j -> addScaled(ip1, v, i, j, uiEx.dot(Tensor.of(v.stream().skip(ip1).map(row -> row.Get(j))))));
    }
    Scalar z = p.one().zero();
    IntStream.range(ip1, cols).forEach(j -> v.set(z, i, j));
    v.stream().skip(ip1).forEach(vj -> vj.set(z, i));
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

  private static void addScaled(int l, Tensor v, int i, int j, Tensor s) {
    v.stream().skip(l).forEach(vk -> vk.set(s.multiply(vk.Get(i))::add, j));
  }
}
