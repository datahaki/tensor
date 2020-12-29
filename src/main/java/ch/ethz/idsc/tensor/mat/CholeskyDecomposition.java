// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;

/** The Cholesky decomposition of a hermitian matrix establishes matrices L and D with
 * 
 * <code>matrix == L . D . L*</code>
 * 
 * <code>matrix == getL().dot(getD().pmul(ConjugateTranspose.of(getL())))</code>
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
   * @throws TensorRuntimeException if matrix is not hermitian, or decomposition failed */
  static CholeskyDecomposition of(Tensor matrix) {
    return new CholeskyDecompositionImpl(matrix, Tolerance.CHOP);
  }

  /** @return lower triangular matrix L */
  Tensor getL();

  /** @return vector of diagonal entries of D */
  Tensor diagonal();

  /** @return determinant of matrix */
  Scalar det();

  /** @param b
   * @return Inverse.of(matrix).dot(b) */
  Tensor solve(Tensor b);
}
