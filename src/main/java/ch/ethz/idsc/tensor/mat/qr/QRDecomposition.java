// code by jph
package ch.ethz.idsc.tensor.mat.qr;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.ConjugateTranspose;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.LeastSquares;

/** QRDecomposition is not consistent with Mathematica.
 * 
 * <p>The tensor library factors a matrix
 * {q, r} = Tensor::QRDecomposition[matrix] such that q.r == matrix.
 * whereas in Mathematica:
 * {q, r} = Mathematica::QRDecomposition[matrix] and then ConjugateTranspose[q].r == matrix.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/QRDecomposition.html">QRDecomposition</a>
 * 
 * @see LeastSquares */
public interface QRDecomposition {
  /** householder reflections with highest numerical stability
   * 
   * @param matrix of dimensions n x m
   * @return qr-decomposition of given matrix
   * @throws Exception if input is not a non-empty rectangular matrix */
  static QRDecomposition of(Tensor matrix) {
    return of(matrix, QRSignOperators.STABILITY);
  }

  /** @param matrix not necessarily square
   * @return qr-decomposition of matrix
   * @throws Exception for input that is not "almost"-orthogonal */
  static QRDecomposition of(Tensor matrix, QRSignOperator qrSignOperator) {
    return of(matrix, IdentityMatrix.of(matrix.length()), qrSignOperator);
  }

  /** @param matrix
   * @param qInv0 for initialization of "Q-Inverse"
   * @param qrSignOperator
   * @return */
  static QRDecomposition of(Tensor matrix, Tensor qInv0, QRSignOperator qrSignOperator) {
    return new QRDecompositionImpl(matrix, qInv0, qrSignOperator);
  }

  /***************************************************/
  /** @return upper triangular matrix */
  Tensor getR();

  /** @return orthogonal matrix
   * @see ConjugateTranspose */
  Tensor getQTranspose();

  /** @return orthogonal matrix */
  Tensor getQ();

  /** @return determinant of matrix */
  Scalar det();

  /** @return least squares solution x with matrix.dot(x) ~ b
   * @throws Exception if rank of matrix is not maximal
   * @throws Exception if n < m */
  Tensor pseudoInverse();
}
