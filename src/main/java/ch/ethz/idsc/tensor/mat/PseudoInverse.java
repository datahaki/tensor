// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.sca.Chop;

/** The pseudo inverse is the least squares solution x to
 * <pre>
 * matrix . x ~ identity matrix
 * </pre>
 * 
 * Therefore, the following relation holds
 * <pre>
 * PseudoInverse[matrix] == LeastSquares[matrix, IdentityMatrix[matrix.length()]]
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/PseudoInverse.html">PseudoInverse</a>
 *
 * @see Inverse
 * @see LeastSquares */
public enum PseudoInverse {
  ;
  /** @param matrix of dimensions n x m
   * @return pseudo inverse of dimensions m x n */
  public static Tensor of(Tensor matrix) {
    if (ExactTensorQ.of(matrix))
      try {
        usingCholesky(matrix);
      } catch (Exception exception) {
        // ---
      }
    return LeastSquares.of(matrix, IdentityMatrix.of(matrix.length()));
  }

  /***************************************************/
  /** Hint: function exists to avoid dot product with the identity matrix
   * 
   * @param matrix
   * @return */
  public static Tensor usingCholesky(Tensor matrix) {
    int n = matrix.length();
    Tensor mt = ConjugateTranspose.of(matrix);
    int m = mt.length();
    return m <= n //
        ? CholeskyDecomposition.of(mt.dot(matrix)).solve(mt)
        : ConjugateTranspose.of(CholeskyDecomposition.of(matrix.dot(mt)).solve(matrix));
  }

  /***************************************************/
  /** Hint: computing the pseudo-inverse using the QR decomposition is generally faster
   * than when using the singular value decomposition.
   * 
   * @param matrix with maximal rank
   * @return pseudoinverse of given matrix
   * @throws Exception if matrix does not have maximal rank */
  public static Tensor usingQR(Tensor matrix) {
    return usingQR(matrix, matrix.length(), Unprotect.dimension1(matrix));
  }

  private static Tensor usingQR(Tensor matrix, int n, int m) {
    return m <= n //
        ? LeastSquares.usingQR(matrix, IdentityMatrix.of(n)) //
        : Transpose.of(LeastSquares.usingQR(Transpose.of(matrix), IdentityMatrix.of(m)));
  }

  /***************************************************/
  /** Remark: Entries of given matrix may be of type {@link Quantity} with identical {@link Unit}.
   * 
   * @param matrix of arbitrary dimension and rank
   * @return pseudoinverse of given matrix */
  public static Tensor usingSvd(Tensor matrix) {
    return usingSvd(matrix, Tolerance.CHOP);
  }

  /** @param matrix
   * @param chop
   * @return */
  public static Tensor usingSvd(Tensor matrix, Chop chop) {
    return usingSvd(matrix, chop, matrix.length(), Unprotect.dimension1(matrix));
  }

  /* package */ static Tensor usingSvd(Tensor matrix, Chop chop, int n, int m) {
    return m <= n //
        ? of(SingularValueDecomposition.of(matrix), chop) //
        : Transpose.of(of(SingularValueDecomposition.of(Transpose.of(matrix)), chop));
  }

  /** @param svd
   * @return pseudoinverse of matrix determined by given svd */
  public static Tensor of(SingularValueDecomposition svd) {
    return of(svd, Tolerance.CHOP);
  }

  /** @param svd
   * @param chop
   * @return pseudoinverse of matrix determined by given svd */
  public static Tensor of(SingularValueDecomposition svd, Chop chop) {
    Tensor wi = SingularValueList.inverted(svd, chop);
    return Tensor.of(svd.getV().stream().map(wi::pmul)).dot(Transpose.of(svd.getU()));
  }
}
