// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.mat.LinearSolve;
import ch.ethz.idsc.tensor.mat.Tolerance;

/* package */ enum MatrixSqrtNewton {
  ;
  private static final int MAX_ITERATIONS = 250;
  private static final Scalar HALF = RealScalar.of(0.5);

  /** Reference:
   * Matrix Computations
   * Section 9.4 "The Sign, Square Root, and Log of a Matrix"
   * 
   * @param matrix square using Newton's method
   * @return */
  static Tensor of(Tensor matrix) {
    Tensor xp = matrix;
    for (int count = 0; count < MAX_ITERATIONS; ++count) {
      Tensor xn = xp.add(LinearSolve.of(xp, matrix)).multiply(HALF);
      if (Tolerance.CHOP.isClose(xp, xn))
        return xp;
      xp = xn;
    }
    throw TensorRuntimeException.of(matrix);
  }
}
