// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.mat.SymmetricMatrixQ;
import ch.ethz.idsc.tensor.mat.Tolerance;

/** Hint: does not work for matrices with quantity */
public interface MatrixSqrt {
  /** Hint: if matrix is symmetric, use {@link #ofSymmetric(Tensor)}
   * 
   * @param matrix square with no negative, or zero eigenvalues
   * @return sqrt of given matrix */
  static MatrixSqrt of(Tensor matrix) {
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
