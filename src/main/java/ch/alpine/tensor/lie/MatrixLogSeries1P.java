// code by jph
package ch.alpine.tensor.lie;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Integers;

/* package */ enum MatrixLogSeries1P implements TensorUnaryOperator {
  FUNCTION;

  private static final int MAX_ITERATIONS = 96;

  /** @param x square matrix with spectral radius below 1
   * @return log[ I + x ]
   * @throws Exception if given matrix is non-square
   * @see Math#log1p(double) */
  @Override
  public Tensor apply(Tensor x) {
    Tensor nxt = x;
    Tensor sum = nxt;
    for (int k = 2; k < MAX_ITERATIONS; ++k) {
      nxt = nxt.dot(x);
      Scalar den = DoubleScalar.of(Integers.isEven(k) ? -k : k);
      if (sum.equals(sum = sum.add(nxt.divide(den))))
        return sum;
    }
    throw TensorRuntimeException.of(x); // insufficient convergence
  }
}
