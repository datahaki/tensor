// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.mat.PositiveDefiniteMatrixQ;
import ch.ethz.idsc.tensor.mat.SymmetricMatrixQ;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.Sign;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/MatrixLog.html">MatrixLog</a> */
public enum MatrixLog {
  ;
  /** Hint: currently only matrices of dimensions 2 x 2 are supported
   * as well as symmetric positive definite matrices
   * 
   * @param matrix
   * @return
   * @throws Exception if computation is not supported for given matrix */
  public static Tensor of(Tensor matrix) {
    int dim1 = Unprotect.dimension1(matrix);
    if (matrix.length() == 2) {
      if (dim1 == 2)
        return MatrixLog2.of(matrix);
    }
    if (SymmetricMatrixQ.of(matrix))
      return ofSymmetric(matrix);
    throw new UnsupportedOperationException();
  }

  /** Hint: use {@link Symmetrize} on result for extra precision
   * 
   * @param matrix symmetric with all positive eigenvalues
   * @return
   * @see PositiveDefiniteMatrixQ */
  public static Tensor ofSymmetric(Tensor matrix) {
    return StaticHelper.ofSymmetric(matrix, MatrixLog::logPositive);
  }

  // helper function
  private static Scalar logPositive(Scalar scalar) {
    return Log.FUNCTION.apply(Sign.requirePositive(scalar));
  }
}
