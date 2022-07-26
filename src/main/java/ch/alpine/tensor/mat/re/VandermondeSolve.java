// code by jph
package ch.alpine.tensor.mat.re;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.qty.LenientAdd;

/** Reference:
 * G. B. Rybicki */
public enum VandermondeSolve {
  ;
  /** @param x vector with non-duplicate entries
   * @param q vector of the same length as x
   * @return LinearSolve.of(Transpose.of(VandermondeMatrix.of(x)), q)
   * @throws Exception if entries in x are not unique */
  public static Tensor of(Tensor x, Tensor q) {
    int n = q.length();
    VectorQ.requireLength(x, n);
    VectorQ.require(q);
    if (n == 1)
      return q.copy();
    Tensor c = x.map(s -> s.one().zero());
    c.set(x.Get(0).negate(), n - 1);
    for (int i = 1; i < n; ++i) {
      Scalar xx = x.Get(i).negate();
      for (int j = n - 1 - i; j < n - 1; ++j) {
        int fj = j;
        // c.set(xx.multiply(c.Get(fj + 1))::add, j);
        // TODO TENSOR MAT check for simplifications
        c.set(s -> LenientAdd.of(s, xx.multiply(c.Get(fj + 1))), j);
      }
      c.set(xx::add, n - 1);
    }
    Tensor w = Tensors.reserve(n);
    for (int i = 0; i < n; ++i) {
      Scalar xx = x.Get(i);
      Scalar t = xx.one();
      Scalar b = xx.one();
      Scalar s = q.Get(n - 1);
      for (int k = n - 1; k > 0; k--) {
        b = c.Get(k).add(xx.multiply(b));
        s = s.add(q.Get(k - 1).multiply(b));
        t = xx.multiply(t).add(b);
      }
      if (Scalars.isZero(t))
        throw new Throw(x, q);
      w.append(s.divide(t));
    }
    return w;
  }
}
