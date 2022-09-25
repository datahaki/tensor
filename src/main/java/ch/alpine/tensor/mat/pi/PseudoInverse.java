// code by jph
package ch.alpine.tensor.mat.pi;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.MatrixDotConjugateTranspose;
import ch.alpine.tensor.mat.MatrixDotTranspose;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.cd.CholeskyDecomposition;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.mat.sv.SingularValueList;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Im;

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
    if (ExactTensorQ.of(matrix) || //
        !Unprotect.isUnitUnique(matrix))
      try {
        return usingCholesky(matrix);
      } catch (Exception exception) {
        // matrix does not have maximal rank
      }
    boolean complex = matrix.flatten(1) //
        .map(Scalar.class::cast) //
        .map(Im.FUNCTION) //
        .anyMatch(Scalars::nonZero);
    if (complex)
      return BenIsraelCohen.of(matrix);
    return usingSvd(matrix);
  }

  // ---
  /** Quote: "A^+ could also be computed directly from A by modifying the elimination steps
   * that usually produce A^-1. However each step of arithmetic would have to be exact! You
   * need to distinguish exact zeros from small nonzeros. That is the hard part of A^+."
   * 
   * @param matrix with maximal rank
   * @return
   * @throws Exception if given matrix does not have maximal rank */
  public static Tensor usingCholesky(Tensor matrix) {
    int n = matrix.length();
    int m = Unprotect.dimension1Hint(matrix);
    if (m <= n) {
      Tensor mt = ConjugateTranspose.of(matrix);
      return CholeskyDecomposition.of(mt.dot(matrix)).solve(mt);
    }
    return ConjugateTranspose.of(CholeskyDecomposition.of( //
        MatrixDotConjugateTranspose.of(matrix)).solve(matrix));
  }

  // ---
  /** Hint: computing the pseudo-inverse using the QR decomposition is
   * possible for matrices of maximal rank, and is generally faster than
   * when using the singular value decomposition.
   * 
   * @param matrix with maximal rank
   * @return pseudoinverse of given matrix
   * @throws Exception if matrix does not have maximal rank */
  @PackageTestAccess
  static Tensor usingQR(Tensor matrix) {
    return usingQR(matrix, matrix.length(), Unprotect.dimension1Hint(matrix));
  }

  private static Tensor usingQR(Tensor matrix, int n, int m) {
    return m <= n //
        ? LeastSquares.usingQR(matrix, IdentityMatrix.of(n)) //
        : Transpose.of(LeastSquares.usingQR(Transpose.of(matrix), IdentityMatrix.of(m)));
  }

  // ---
  /** Quote from Mathematica: "With the default setting Tolerance->Automatic,
   * singular values are dropped when they are less than 100 times 10^-p,
   * where p is Precision[m]."
   * In Mathematica the tolerance is 1.1102230246251578*^-14. */
  private static final Chop CHOP = Tolerance.CHOP; // 10^-12

  /** Remark: Entries of given matrix may be of type {@link Quantity} with identical {@link Unit}.
   * 
   * @param matrix of arbitrary dimension and rank
   * @return pseudoinverse of given matrix */
  @PackageTestAccess
  static Tensor usingSvd(Tensor matrix) {
    return usingSvd(matrix, CHOP);
  }

  /** @param matrix
   * @param chop
   * @return */
  private static Tensor usingSvd(Tensor matrix, Chop chop) {
    return usingSvd(matrix, chop, matrix.length(), Unprotect.dimension1Hint(matrix));
  }

  /* package */ static Tensor usingSvd(Tensor matrix, Chop chop, int n, int m) {
    return m <= n //
        ? of(SingularValueDecomposition.of(matrix), chop) //
        : Transpose.of(of(SingularValueDecomposition.of(Transpose.of(matrix)), chop));
  }

  /** @param svd
   * @return pseudoinverse of matrix determined by given svd */
  public static Tensor of(SingularValueDecomposition svd) {
    return of(svd, CHOP);
  }

  /** @param svd
   * @param chop
   * @return pseudoinverse of matrix determined by given svd */
  public static Tensor of(SingularValueDecomposition svd, Chop chop) {
    Tensor wi = SingularValueList.inverted(svd, chop);
    return MatrixDotTranspose.of(Tensor.of(svd.getV().stream().map(Times.operator(wi))), svd.getU());
  }
}
