// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Unprotect;

/** TODO goal: decomposition of A = Q.S
 * where Q is orthogonal and S is symmetric/hermitian positive semi-definite
 * 
 * Reference:
 * "Meshless Deformations Based on Shape Matching"
 * by M. Mueller, B. Heidelberger, M. Teschner, M. Gross, 2005 */
public interface PolarDecomposition {
  /** also works for complex input
   * 
   * @param matrix of dimensions k x n with k <= n
   * @return */
  static PolarDecomposition of(Tensor matrix) {
    if (matrix.length() <= Unprotect.dimension1Hint(matrix))
      return new PolarDecompositionSqrt(matrix);
    throw TensorRuntimeException.of(matrix);
  }

  /** @return orthogonal/unitary matrix of dimensions k x n with determinant either +1 or -1
   * @see OrthogonalMatrixQ
   * @see UnitaryMatrixQ */
  Tensor getQ();

  /** Quote from Stang: "S is the symmetric positive definite square root of A^T.A"
   * 
   * @return symmetric/hermitian positive semidefinite matrix k x k
   * @see PositiveSemidefiniteMatrixQ */
  Tensor getS();
}
