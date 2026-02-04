// code by jph
package ch.alpine.tensor.mat.ex;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.lie.Symmetrize;
import ch.alpine.tensor.mat.PositiveDefiniteMatrixQ;
import ch.alpine.tensor.mat.ev.Eigensystem;
import ch.alpine.tensor.sca.exp.Log;

/** Hint: implementation uses inverse of the scaling and squaring procedure that
 * involves repeated matrix square roots.
 * 
 * References:
 * "Matrix Computations" 4th Edition
 * by Gene H. Golub, Charles F. Van Loan, 2012
 * 
 * "Approximating the Logarithm of a Matrix to Specified Accuracy"
 * by Sheung Hun Cheng, Nicholas J. Higham, Charles S. Kenny, Alan J. Laub 2001
 *
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/MatrixLog.html">MatrixLog</a>
 * 
 * @see MatrixExp */
public enum MatrixLog {
  ;
  public static final ThreadLocal<Integer> MatrixLog_MAX_EXPONENT = ThreadLocal.withInitial(() -> 20);
  static final Scalar RHO_MAX = RealScalar.of(0.6);

  /** Hint: currently only matrices of dimensions 2 x 2 are supported
   * as well as symmetric positive definite matrices
   * 
   * @param matrix
   * @return
   * @throws Exception if computation is not supported for given matrix */
  public static Tensor of(Tensor matrix) {
    return switch (matrix.length()) {
    case 1 -> MatrixLogs._1(matrix);
    case 2 -> MatrixLogs._2(matrix);
    default -> MatrixLogs.of(matrix);
    };
  }

  /** Hint: use {@link Symmetrize} on result for extra precision
   * 
   * @param matrix symmetric with all positive eigenvalues
   * @return
   * @see PositiveDefiniteMatrixQ */
  public static Tensor ofSymmetric(Tensor matrix) {
    return Eigensystem.ofSymmetric(matrix).map(Log.FUNCTION);
  }

  /** @param matrix
   * @return */
  public static Tensor ofHermitian(Tensor matrix) {
    return Eigensystem.ofHermitian(matrix).map(Log.FUNCTION);
  }
}
