// code by jph
// https://stats.stackexchange.com/questions/66088/analysis-with-complex-data-anything-different
package ch.alpine.tensor.mat.pi;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.MatrixDotConjugateTranspose;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.cd.CholeskyDecomposition;
import ch.alpine.tensor.mat.qr.QRDecomposition;
import ch.alpine.tensor.mat.qr.QRSignOperators;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.mat.sv.SingularValueList;
import ch.alpine.tensor.sca.Chop;

/** least squares solution x that approximates
 * <pre>
 * matrix . x ~ b
 * </pre>
 * 
 * The general solution is given by
 * <pre>
 * x == PseudoInverse[m] . b
 * </pre>
 * 
 * However, the computation of the pseudo-inverse can often be avoided.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/LeastSquares.html">LeastSquares</a>
 * 
 * @see CholeskyDecomposition
 * @see QRDecomposition
 * @see SingularValueDecomposition
 * @see PseudoInverse */
public enum LeastSquares {
  ;
  private static final Chop CHOP = Tolerance.CHOP;

  /** If given matrix and b are in exact precision and the matrix has rank m
   * the CholeskyDecomposition produces x in exact precision.
   * 
   * @param matrix of size n x m
   * @param b
   * @return x with matrix.dot(x) ~ b */
  public static Tensor of(Tensor matrix, Tensor b) {
    boolean assumeRankM = true;
    if (ExactTensorQ.of(matrix))
      try {
        return PseudoInverse.usingCholesky(matrix).dot(b);
      } catch (Exception exception) {
        assumeRankM = false; // rank is not maximal
      }
    int n = matrix.length();
    int m = Unprotect.dimension1(matrix);
    if (assumeRankM)
      try {
        return usingQR(matrix, b, n, m);
      } catch (Exception exception) {
        assumeRankM = false; // rank is not maximal
      }
    return m <= n //
        ? usingSvd(matrix, b)
        : PseudoInverse.usingSvd(matrix, CHOP, n, m).dot(b);
  }

  // ---
  /** @param matrix
   * @param b
   * @return x with matrix.dot(x) ~ b
   * @throws Exception if matrix does not have maximal rank */
  public static Tensor usingQR(Tensor matrix, Tensor b) {
    return usingQR(matrix, b, matrix.length(), Unprotect.dimension1(matrix));
  }

  private static Tensor usingQR(Tensor matrix, Tensor b, int n, int m) {
    return m <= n //
        ? _usingQR(matrix, b)
        : ConjugateTranspose.of(_usingQR(MatrixDotConjugateTranspose.of(matrix), matrix)).dot(b);
  }

  private static Tensor _usingQR(Tensor matrix, Tensor b) {
    return QRDecomposition.of(matrix, b, QRSignOperators.STABILITY).pseudoInverse();
  }

  // ---
  /** when m does not have full rank, and for numerical stability
   * the function usingSvd(...) is preferred over the function usingLinearSolve(...)
   * 
   * @param matrix with rows >= cols
   * @param b
   * @return x with matrix.dot(x) ~ b */
  public static Tensor usingSvd(Tensor matrix, Tensor b) {
    return of(SingularValueDecomposition.of(matrix), b);
  }

  /** @param svd of matrix
   * @param b
   * @return pseudo inverse of given matrix dot b */
  public static Tensor of(SingularValueDecomposition svd, Tensor b) {
    if (VectorQ.of(b)) { // when b is vector then bypass construction of pseudo inverse matrix
      Tensor wi = SingularValueList.inverted(svd, CHOP);
      return svd.getV().dot(wi.pmul(b.dot(svd.getU()))); // U^t . b == b . U
    }
    return PseudoInverse.of(svd, CHOP).dot(b);
  }
}
