// code by jph
package ch.alpine.tensor.mat.ex;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.LinearSolve;

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
    throw new Throw(matrix);
  }
}
