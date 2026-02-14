// code by jph
package ch.alpine.tensor.mat.re;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.mat.SquareMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.pi.PseudoInverse;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/LinearSolveFunction.html">LinearSolveFunction</a> */
public enum LinearSolveFunction {
  ;
  /** @param matrix
   * @return */
  public static TensorUnaryOperator of(Tensor matrix) {
    if (SquareMatrixQ.INSTANCE.test(matrix))
      try {
        return Inverse.of(matrix)::dot;
      } catch (Exception exception) {
        // ---
      }
    try {
      Tensor pinv = PseudoInverse.of(matrix);
      return b -> {
        Tensor x = pinv.dot(b);
        Tolerance.CHOP.requireClose(matrix.dot(x), b);
        return x;
      };
    } catch (Exception exception) {
      // ---
    }
    return b -> LinearSolve.any(matrix, b);
  }
}
