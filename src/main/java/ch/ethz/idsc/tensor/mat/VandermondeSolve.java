// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.VectorQ;

/** Reference: G. B. Rybicki */
public enum VandermondeSolve {
  ;
  /** @param x vector with non-duplicate entries
   * @param q vector
   * @return LinearSolve.of(Transpose.of(VandermondeMatrix.of(x)), q)
   * @throws Exception if entries in x are not unique */
  public static Tensor of(Tensor x, Tensor q) {
    int n = q.length();
    VectorQ.requireLength(x, n);
    VectorQ.require(q);
    if (n == 1)
      return q.copy();
    Tensor c = Array.zeros(n);
    c.set(x.get(0).negate(), n - 1);
    for (int i = 1; i < n; ++i) {
      Scalar xx = x.Get(i).negate();
      for (int j = n - 1 - i; j < n - 1; ++j) {
        int fj = j;
        c.set(xx.multiply(c.Get(fj + 1))::add, j);
      }
      c.set(xx::add, n - 1);
    }
    Tensor w = Tensors.reserve(n);
    for (int i = 0; i < n; ++i) {
      Scalar xx = x.Get(i);
      Scalar t = RealScalar.ONE;
      Scalar b = RealScalar.ONE;
      Scalar s = q.Get(n - 1);
      for (int k = n - 1; k > 0; k--) {
        b = c.Get(k).add(xx.multiply(b));
        s = s.add(q.Get(k - 1).multiply(b));
        t = xx.multiply(t).add(b);
      }
      if (Scalars.isZero(t))
        throw TensorRuntimeException.of(x, q);
      w.append(s.divide(t));
    }
    return w;
  }
}
