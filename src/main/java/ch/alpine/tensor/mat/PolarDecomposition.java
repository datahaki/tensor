// code by jph
package ch.alpine.tensor.mat;

import java.io.Serializable;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.lie.MatrixSqrt;

/** decomposition of A = S.R
 * where S is symmetric, and R is orthogonal
 * 
 * Reference:
 * "Meshless Deformations Based on Shape Matching"
 * by M. Mueller, B. Heidelberger, M. Teschner, M. Gross, 2005 */
public class PolarDecomposition implements Serializable {
  /** also works for complex input
   * 
   * @param matrix of dimensions k x n with k <= n
   * @return */
  public static PolarDecomposition of(Tensor matrix) {
    if (matrix.length() <= Unprotect.dimension1Hint(matrix))
      return new PolarDecomposition(matrix);
    throw TensorRuntimeException.of(matrix);
  }

  // ---
  private final Tensor matrix;
  private final MatrixSqrt matrixSqrt;

  private PolarDecomposition(Tensor matrix) {
    this.matrix = matrix;
    matrixSqrt = MatrixSqrt.of(MatrixDotConjugateTranspose.of(matrix));
  }

  /** @return symmetric (or Hermitian) matrix k x k */
  public Tensor getS() {
    return matrixSqrt.sqrt();
  }

  /** @return orthogonal (or unitary) matrix of dimensions k x n with determinant either +1 or -1
   * @see OrthogonalMatrixQ
   * @see UnitaryMatrixQ */
  public Tensor getR() {
    return matrixSqrt.sqrt_inverse().dot(matrix);
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s]", getClass().getSimpleName(), Tensors.message(getS(), getR()));
  }
}
