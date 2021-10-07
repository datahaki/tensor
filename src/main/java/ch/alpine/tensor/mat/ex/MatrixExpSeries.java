// code by jph
package ch.alpine.tensor.mat.ex;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.N;

/* package */ enum MatrixExpSeries implements TensorUnaryOperator {
  FUNCTION;

  /** with scaling the series typically converges in few steps */
  private static final int MAX_ITERATIONS = 128;

  /** @param matrix square
   * @return
   * @throws Exception if given matrix is non-square */
  @Override
  public Tensor apply(Tensor matrix) {
    int n = matrix.length();
    Tensor nxt = matrix;
    Tensor sum = StaticHelper.IDENTITY_MATRIX.apply(n).add(nxt);
    for (int k = 2; k <= n; ++k) {
      nxt = nxt.dot(matrix).divide(RealScalar.of(k));
      sum = sum.add(nxt);
      if (Chop.NONE.allZero(nxt))
        return sum;
    }
    sum = N.DOUBLE.of(sum); // switch to numeric precision
    for (int k = n + 1; k < MAX_ITERATIONS; ++k) {
      nxt = nxt.dot(matrix).divide(RealScalar.of(k));
      if (sum.equals(sum = sum.add(nxt)))
        return sum;
    }
    throw TensorRuntimeException.of(matrix); // insufficient convergence
  }
}
