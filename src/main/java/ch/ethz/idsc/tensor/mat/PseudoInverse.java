// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Imag;

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
 * The pseudoinverse satisfies
 * <pre>
 * PseudoInverse[matrix] == PseudoInverse[ matrix^T . matrix ] . matrix^T
 * </pre>
 * or short
 * <pre>
 * matrix^+ == (matrix^T . matrix)^+ . matrix^T
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
        return usingCholesky(matrix);
      } catch (Exception exception) {
        // matrix does not have maximal rank
      }
    // return LeastSquares.of(matrix, IdentityMatrix.of(matrix.length()));
    boolean complex = matrix.flatten(2).map(Scalar.class::cast).map(Imag.FUNCTION).anyMatch(Scalars::nonZero);
    if (complex)
      return BenIsraelCohen.of(matrix);
    return usingSvd(matrix);
  }

  /***************************************************/
  /** @param matrix with maximal rank
   * @return
   * @throws Exception if given matrix does not have maximal rank */
  /* package */ static Tensor usingCholesky(Tensor matrix) {
    int n = matrix.length();
    Tensor mt = ConjugateTranspose.of(matrix);
    int m = mt.length();
    return m <= n //
        ? CholeskyDecomposition.of(mt.dot(matrix)).solve(mt)
        : ConjugateTranspose.of(CholeskyDecomposition.of(matrix.dot(mt)).solve(matrix));
  }

  /***************************************************/
  /** Hint: computing the pseudo-inverse using the QR decomposition is
   * possible for matrices of maximal rank, and is generally faster than
   * when using the singular value decomposition.
   * 
   * @param matrix with maximal rank
   * @return pseudoinverse of given matrix
   * @throws Exception if matrix does not have maximal rank */
  /* package */ static Tensor usingQR(Tensor matrix) {
    return usingQR(matrix, matrix.length(), Unprotect.dimension1(matrix));
  }

  private static Tensor usingQR(Tensor matrix, int n, int m) {
    return m <= n //
        ? LeastSquares.usingQR(matrix, IdentityMatrix.of(n)) //
        : Transpose.of(LeastSquares.usingQR(Transpose.of(matrix), IdentityMatrix.of(m)));
  }

  /***************************************************/
  /** Quote from Mathematica: "With the default setting Tolerance->Automatic,
   * singular values are dropped when they are less than 100 times 10^-p,
   * where p is Precision[m]."
   * In Mathematica the tolerance is 1.1102230246251578*^-14. */
  private static final Chop TOLERANCE = Tolerance.CHOP; // 10^-12

  /** Remark: Entries of given matrix may be of type {@link Quantity} with identical {@link Unit}.
   * 
   * @param matrix of arbitrary dimension and rank
   * @return pseudoinverse of given matrix */
  /* package */ static Tensor usingSvd(Tensor matrix) {
    return usingSvd(matrix, TOLERANCE);
  }

  /** @param matrix
   * @param chop
   * @return */
  private static Tensor usingSvd(Tensor matrix, Chop chop) {
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
    return of(svd, TOLERANCE);
  }

  /** @param svd
   * @param chop
   * @return pseudoinverse of matrix determined by given svd */
  public static Tensor of(SingularValueDecomposition svd, Chop chop) {
    Tensor wi = SingularValueList.inverted(svd, chop);
    return Tensor.of(svd.getV().stream().map(wi::pmul)).dot(Transpose.of(svd.getU()));
  }
}
