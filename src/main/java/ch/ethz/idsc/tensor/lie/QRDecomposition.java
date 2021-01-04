// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

/** QRDecomposition is not consistent with Mathematica.
 * 
 * <p>The tensor library factors a matrix
 * {q, r} = Tensor::QRDecomposition[matrix] such that q.r == matrix.
 * whereas in Mathematica:
 * {q, r} = Mathematica::QRDecomposition[matrix] and then ConjugateTranspose[q].r == matrix.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/QRDecomposition.html">QRDecomposition</a> */
public interface QRDecomposition {
  /** householder reflections with highest numerical stability
   * 
   * @param matrix of dimensions n x m
   * @return qr-decomposition of given matrix */
  static QRDecomposition of(Tensor matrix) {
    return of(matrix, QRSignOperators.STABILITY);
  }

  /** @param matrix not necessarily square
   * @return qr-decomposition of matrix
   * @throws Exception for input that is not "almost"-orthogonal */
  static QRDecomposition of(Tensor matrix, QRSignOperator qrSignOperator) {
    return new QRDecompositionImpl(matrix, IdentityMatrix.of(matrix.length()), qrSignOperator);
  }

  /** @param matrix
   * @param b
   * @param qrSignOperator
   * @return least squares solution x that approximates matrix . x ~ b */
  static Tensor solve(Tensor matrix, Tensor b, QRSignOperator qrSignOperator) {
    return new QRDecompositionImpl(matrix, b, qrSignOperator).eliminate();
  }

  /** @return orthogonal matrix */
  Tensor getInverseQ();

  /** @return upper triangular matrix */
  Tensor getR();

  /** @return orthogonal matrix */
  Tensor getQ();

  /** @return determinant of matrix */
  Scalar det();
}
