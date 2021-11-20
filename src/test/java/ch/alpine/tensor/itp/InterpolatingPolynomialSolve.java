// code by jph
package ch.alpine.tensor.itp;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.lie.Quaternion;

/** does not work for {@link Quaternion}
 * 
 * Reference:
 * G. B. Rybicki */
/* package */ enum InterpolatingPolynomialSolve {
  ;
  /** @param x vector with non-duplicate entries
   * @param y vector of the same length as x
   * @return LinearSolve.of(VandermondeMatrix.of(x), y)
   * @throws Exception if entries in x are not unique */
  public static Tensor of(Tensor x, Tensor y) {
    int n = y.length();
    VectorQ.requireLength(x, n);
    VectorQ.require(y);
    Tensor s = x.map(Scalar::zero);
    // Tensor s = x.map(r->r.one().zero());
    s.set(x.Get(0).negate(), n - 1);
    for (int i = 1; i < n; ++i) {
      Scalar xx = x.Get(i).negate();
      for (int j = n - 1 - i; j < n - 1; ++j) {
        int fj = j;
        s.set(xx.multiply(s.Get(fj + 1))::add, j);
      }
      s.set(xx::add, n - 1);
    }
    Tensor c = x.map(Scalar::zero);
    for (int j = 0; j < n; ++j) {
      Scalar p = RealScalar.of(n);
      for (int k = n - 1; 0 < k; --k)
        p = p.multiply(x.Get(j)).add(RealScalar.of(k).multiply(s.Get(k)));
      Scalar ff = y.Get(j).divide(p);
      Scalar b = RealScalar.ONE;
      for (int k = n - 1; 0 <= k; --k) {
        c.set(b.multiply(ff)::add, k);
        b = s.Get(k).add(x.get(j).multiply(b));
      }
    }
    return c;
  }
}
