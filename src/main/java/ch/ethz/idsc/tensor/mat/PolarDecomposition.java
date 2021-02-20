// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.Serializable;
import java.util.List;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.Dimensions;
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

  /** @param matrix of dimensions k x n with k <= n
   * @return */
  public static PolarDecomposition of(Tensor matrix) {
    List<Integer> list = Dimensions.of(matrix);
    if (list.get(0) <= list.get(1))
      return new PolarDecomposition(matrix);
    throw TensorRuntimeException.of(matrix);
  }

  /***************************************************/
  private final Tensor matrix;
  private final MatrixSqrt matrixSqrt;

  private PolarDecomposition(Tensor matrix) {
    this.matrix = matrix;
    matrixSqrt = MatrixSqrt.ofSymmetric(MatrixDotTranspose.of(matrix, Conjugate.of(matrix)));
  }

  /** @return orthogonal matrix of dimensions k x n with determinant either +1 or -1 */
  public Tensor getR() {
    return matrixSqrt.sqrt_inverse().dot(matrix);
  }

  /** @return symmetric matrix k x k */
  public Tensor getS() {
    return matrixSqrt.sqrt();
  }
}
