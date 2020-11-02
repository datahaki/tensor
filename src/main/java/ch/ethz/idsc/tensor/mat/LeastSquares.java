// code by jph
// https://stats.stackexchange.com/questions/66088/analysis-with-complex-data-anything-different
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/LeastSquares.html">LeastSquares</a>
 * 
 * @see PseudoInverse */
public enum LeastSquares {
  ;
  /** @param matrix
   * @param b
   * @return x with matrix.dot(x) ~ b */
  public static Tensor of(Tensor matrix, Tensor b) {
    if (ExactTensorQ.of(matrix))
      try {
        return usingLinearSolve(matrix, b);
      } catch (Exception exception) {
        // rank deficient
      }
    return usingSvd(matrix, b);
  }

  /** @param matrix with rows >= cols, and maximum rank
   * @param b
   * @return x with matrix.dot(x) ~ b
   * @throws Exception if matrix does not have maximum rank */
  public static Tensor usingLinearSolve(Tensor matrix, Tensor b) {
    Tensor mt = ConjugateTranspose.of(matrix);
    return LinearSolve.of(mt.dot(matrix), mt.dot(b));
  }

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
      Tensor wi = SingularValueList.inverted(svd, Tolerance.CHOP);
      return svd.getV().dot(wi.pmul(b.dot(svd.getU()))); // U^t . b == b . U
    }
    return PseudoInverse.of(svd).dot(b);
  }
}
