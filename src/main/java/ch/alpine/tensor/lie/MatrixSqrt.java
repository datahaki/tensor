// code by jph
package ch.alpine.tensor.lie;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.Tolerance;

/** Hint: does not work for matrices with quantity */
public interface MatrixSqrt {
  /** Hint: if matrix is symmetric, use {@link #ofSymmetric(Tensor)}
   * 
   * @param matrix square with no negative, or zero eigenvalues
   * @return sqrt of given matrix */
  static MatrixSqrt of(Tensor matrix) {
    // TODO change logic: check real-symmetric first
    try {
      return new DenmanBeaversDet(matrix, Tolerance.CHOP);
    } catch (Exception exception) {
      // ---
    }
    if (SymmetricMatrixQ.of(matrix))
      return ofSymmetric(matrix);
    throw TensorRuntimeException.of(matrix);
  }

  /** @param matrix symmetric
   * @return sqrt of given matrix
   * @throws Exception if matrix is not symmetric */
  static MatrixSqrt ofSymmetric(Tensor matrix) {
    return new MatrixSqrtSymmetric(matrix);
  }

  /***************************************************/
  /** @return square root of a given matrix */
  Tensor sqrt();

  /** @return inverse of square root of a given matrix */
  Tensor sqrt_inverse();
}
