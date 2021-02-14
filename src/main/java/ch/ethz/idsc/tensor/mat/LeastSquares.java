// code by jph
// https://stats.stackexchange.com/questions/66088/analysis-with-complex-data-anything-different
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.sca.Chop;

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
    int n = matrix.length();
    int m = Unprotect.dimension1(matrix);
    boolean assumeRankM = true;
    if (ExactTensorQ.of(matrix))
      try {
        return usingCholesky(matrix, b, CHOP, n, m);
      } catch (Exception exception) {
        assumeRankM = false; // rank is not maximal
      }
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

  /***************************************************/
  /** Remark: The CholeskyDecomposition is used instead of LinearSolve
   * 
   * @param matrix with maximum rank
   * @param b
   * @return x with matrix.dot(x) ~ b
   * @throws Exception if matrix does not have maximum rank */
  public static Tensor usingCholesky(Tensor matrix, Tensor b) {
    return usingCholesky(matrix, b, CHOP, matrix.length(), Unprotect.dimension1(matrix));
  }

  private static Tensor usingCholesky(Tensor matrix, Tensor b, Chop chop, int n, int m) {
    Tensor mt = ConjugateTranspose.of(matrix);
    return m <= n //
        ? CholeskyDecomposition.of(mt.dot(matrix), CHOP).solve(mt.dot(b))
        : ConjugateTranspose.of(CholeskyDecomposition.of(matrix.dot(mt), CHOP).solve(matrix)).dot(b);
  }

  /***************************************************/
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
        : ConjugateTranspose.of(_usingQR(matrix.dot(ConjugateTranspose.of(matrix)), matrix)).dot(b);
  }

  private static Tensor _usingQR(Tensor matrix, Tensor b) {
    return new QRDecompositionImpl(matrix, b, QRSignOperators.STABILITY).pseudoInverse();
  }

  /***************************************************/
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
