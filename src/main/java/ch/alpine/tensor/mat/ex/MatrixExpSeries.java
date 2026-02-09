// code by jph
package ch.alpine.tensor.mat.ex;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.N;

/* package */ enum MatrixExpSeries implements TensorUnaryOperator {
  FUNCTION;

  /** @param matrix square
   * @return
   * @throws Exception if given matrix is non-square */
  @Override
  public Tensor apply(Tensor matrix) {
    int n = matrix.length();
    Tensor nxt = matrix;
    Tensor sum = IdentityMatrix.inplaceAdd(nxt.copy());
    for (int k = 2; k <= n; ++k) {
      nxt = nxt.dot(matrix).divide(RealScalar.of(k));
      sum = sum.add(nxt);
      if (Chop.NONE.allZero(nxt))
        return sum;
    }
    sum = sum.maps(N.DOUBLE); // switch to numeric precision
    /* with scaling the series typically converges in few steps */
    int max = MatrixExp.MAX_ITERATIONS.get();
    for (int k = n + 1; k < max; ++k) {
      nxt = nxt.dot(matrix).divide(RealScalar.of(k));
      if (sum.equals(sum = sum.add(nxt)))
        return sum;
    }
    throw new Throw(matrix); // insufficient convergence
  }
}
