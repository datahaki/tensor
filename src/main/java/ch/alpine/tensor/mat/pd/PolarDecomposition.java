// code by jph
package ch.alpine.tensor.mat.pd;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.mat.OrthogonalMatrixQ;
import ch.alpine.tensor.mat.PositiveSemidefiniteMatrixQ;
import ch.alpine.tensor.mat.UnitaryMatrixQ;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;

/** Q is unitary and S is symmetric/hermitian positive semi-definite
 * 
 * Reference:
 * "Meshless Deformations Based on Shape Matching"
 * by M. Mueller, B. Heidelberger, M. Teschner, M. Gross, 2005 */
public interface PolarDecomposition {
  /** @param matrix of dimensions k x n
   * @return decomposition of A = S.U */
  static PolarDecomposition su(Tensor matrix) {
    int cols = Unprotect.dimension1Hint(matrix);
    return matrix.length() <= cols //
        ? new SqrtSu(matrix)
        : new SvdSu(SingularValueDecomposition.of(matrix));
  }

  /** @param matrix of dimensions k x n
   * @return decomposition of A = U.S */
  static PolarDecomposition us(Tensor matrix) {
    int cols = Unprotect.dimension1Hint(matrix);
    return matrix.length() <= cols //
        ? new SqrtUs(matrix)
        : new SvdUs(SingularValueDecomposition.of(matrix));
  }

  /** Quote from Strang: "S is the symmetric positive definite square root of A^T.A"
   * 
   * @return symmetric/hermitian positive semidefinite matrix k x k
   * @see PositiveSemidefiniteMatrixQ */
  Tensor getPositiveSemidefinite();

  /** @return orthogonal/unitary matrix of dimensions k x n with determinant either +1 or -1
   * @see OrthogonalMatrixQ
   * @see UnitaryMatrixQ */
  Tensor getUnitary();
}
