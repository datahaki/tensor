// code by jph
package ch.alpine.tensor.mat.cd;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.HermitianMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Chop;

/** The Cholesky decomposition of a hermitian matrix establishes matrices L and D with
 * 
 * <code>matrix == L . D . L*</code>
 * 
 * <code>matrix == getL().dot(Times.of(diagonal(), ConjugateTranspose.of(getL())))</code>
 * 
 * <p>The decomposition is robust for positive definite matrices.
 * 
 * <p>For some hermitian matrices the decomposition cannot be established.
 * An example that fails (also in Mathematica) is <code>{{0, 1}, {1, 0}}</code>.
 * 
 * <p>Remark: Our implementation follows the convention of Wikipedia, as well as
 * Numerical Recipes instead of Mathematica, which returns the upper triangular matrix.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/CholeskyDecomposition.html">CholeskyDecomposition</a>
 * 
 * @see HermitianMatrixQ */
public interface CholeskyDecomposition {
  /** @param matrix hermitian and positive semi-definite matrix
   * @return Cholesky decomposition of matrix
   * @throws Exception if matrix is not hermitian, or decomposition failed */
  static CholeskyDecomposition of(Tensor matrix) {
    return of(matrix, Tolerance.CHOP);
  }

  /** @param matrix hermitian and positive semi-definite matrix
   * @param chop
   * @return Cholesky decomposition of matrix
   * @throws Exception if matrix is not hermitian, or decomposition failed */
  static CholeskyDecomposition of(Tensor matrix, Chop chop) {
    return new CholeskyDecompositionImpl(matrix, chop);
  }

  // ---
  /** @return lower triangular matrix L */
  Tensor getL();

  /** @return vector of diagonal entries of D */
  Tensor diagonal();

  /** @return determinant of matrix */
  Scalar det();

  /** @param b
   * @return Inverse.of(matrix).dot(b) == LinearSolve.of(matrix, b)
   * @throws Exception if any entry of diagonal is zero according to chop */
  Tensor solve(Tensor b);
  // default Tensor map(ScalarUnaryOperator scalarUnaryOperator) {
  // Tensor L = getL();
  // Tensor diagonal = diagonal();
  // Tensor ctL = ConjugateTranspose.of(L);
  // return L.dot(Times.of(diagonal.map(scalarUnaryOperator), ctL));
  // }
}
