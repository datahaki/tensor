// code by jph
package ch.alpine.tensor.mat.pd;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.mat.OrthogonalMatrixQ;
import ch.alpine.tensor.mat.Orthogonalize;
import ch.alpine.tensor.mat.PositiveSemidefiniteMatrixQ;
import ch.alpine.tensor.mat.UnitaryMatrixQ;
import ch.alpine.tensor.mat.ex.MatrixSqrt;

/** Q is unitary and P is symmetric/hermitian positive semi-definite
 * 
 * Reference:
 * "Meshless Deformations Based on Shape Matching"
 * by M. Mueller, B. Heidelberger, M. Teschner, M. Gross, 2005 */
public interface PolarDecomposition {
  /** suitable for matrices of any dimension
   * 
   * Implementation is used in {@link Orthogonalize#usingPD(Tensor)}.
   * 
   * @param matrix of dimensions k x n
   * @return decomposition of matrix = P.U */
  static PolarDecomposition pu(Tensor matrix) {
    int cols = Unprotect.dimension1Hint(matrix);
    return matrix.length() <= cols //
        ? new SqrtPu(matrix)
        : PolarDecompositionSvd.pu(matrix);
  }

  /** Hint: for a matrix A with rows < cols, the decomposition involves
   * the {@link MatrixSqrt} of the rank deficient A^T.A which reduces
   * the numerical precision in practice.
   * 
   * @param matrix of dimensions k x n
   * @return decomposition of matrix = U.P */
  static PolarDecomposition up(Tensor matrix) {
    int cols = Unprotect.dimension1Hint(matrix);
    return matrix.length() <= cols //
        ? new SqrtUp(matrix)
        : PolarDecompositionSvd.up(matrix);
  }

  /** Quote from Strang: "P is the symmetric positive definite square root of A^T.A"
   * 
   * @return symmetric/hermitian positive semi-definite matrix k x k
   * @see PositiveSemidefiniteMatrixQ#ofHermitian(Tensor) */
  Tensor getPositiveSemidefinite();

  /** @return orthogonal/unitary matrix of dimensions k x n with determinant either +1 or -1
   * @see OrthogonalMatrixQ
   * @see UnitaryMatrixQ */
  Tensor getUnitary();
}
