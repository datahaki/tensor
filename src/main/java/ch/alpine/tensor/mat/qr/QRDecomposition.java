// code by jph
package ch.alpine.tensor.mat.qr;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.pi.LeastSquares;

/** QRDecomposition is not consistent with Mathematica.
 * 
 * <p>The tensor library factors a matrix
 * {q, r} = Tensor::QRDecomposition[matrix] such that q.r == matrix.
 * whereas in Mathematica:
 * {q, r} = Mathematica::QRDecomposition[matrix] and then ConjugateTranspose[q].r == matrix.
 * 
 * Our choice is motivated by the fact that the name "qr"-decomposition suggests
 * that the product of q and r gives the original matrix.
 * 
 * Mathematica's choice is probably motivated by the fact that the implementations
 * build ConjugateTranspose[q] instead of q.
 * 
 * Our interface simply provides ConjugateTranspose[q] via the method
 * {@link #getQConjugateTranspose()}.
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

  /** @return upper triangular matrix with respect to column permutation sigma */
  Tensor getR();

  /** @return ConjugateTranspose[getQ()]
   * @see ConjugateTranspose */
  Tensor getQConjugateTranspose();

  /** @return orthogonal matrix */
  Tensor getQ();

  /** @return determinant of matrix */
  Scalar det();

  /** method performs
   * <pre>
   * Inverse[getR()] . getQConjugateTranspose()
   * </pre>
   * 
   * equivalently:
   * <pre>
   * LinearSolve[getR()], getQConjugateTranspose()]
   * </pre>
   * 
   * @return PseudoInverse[matrix], least squares solution x with matrix.dot(x) ~ qInv0
   * @throws Exception if rank of matrix is not maximal
   * @throws Exception if n < m */
  Tensor pseudoInverse();

  /** @return permutation where the i-th element indicates what column in {@link #getR()}
   * to take the diagonal element from */
  int[] sigma();
}
