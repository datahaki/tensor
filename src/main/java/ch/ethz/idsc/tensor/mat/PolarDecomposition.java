// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.MatrixDotTranspose;
import ch.ethz.idsc.tensor.lie.MatrixSqrt;
import ch.ethz.idsc.tensor.sca.Conjugate;

/** decomposition of A = S.R
 * where R is orthogonal, and S is symmetric
 * 
 * Reference:
 * "Meshless Deformations Based on Shape Matching"
 * by M. Mueller, B. Heidelberger, M. Teschner, M. Gross, 2005 */
public class PolarDecomposition implements Serializable {
  private static final long serialVersionUID = 6692615139679469889L;

  /** also works for complex input
   * 
   * @param matrix of dimensions k x n with k <= n
   * @return */
  public static PolarDecomposition of(Tensor matrix) {
    if (matrix.length() <= Unprotect.dimension1Hint(matrix))
      return new PolarDecomposition(matrix);
    throw TensorRuntimeException.of(matrix);
  }

  /***************************************************/
  private final Tensor matrix;
  private final MatrixSqrt matrixSqrt;

  private PolarDecomposition(Tensor matrix) {
    this.matrix = matrix;
    matrixSqrt = MatrixSqrt.of(MatrixDotTranspose.of(matrix, Conjugate.of(matrix)));
  }

  /** @return orthogonal matrix of dimensions k x n with determinant either +1 or -1
   * @see OrthogonalMatrixQ
   * @see UnitaryMatrixQ */
  public Tensor getR() {
    return matrixSqrt.sqrt_inverse().dot(matrix);
  }

  /** @return symmetric matrix k x k */
  public Tensor getS() {
    return matrixSqrt.sqrt();
  }
}
