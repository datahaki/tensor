// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.MatrixDotTranspose;
import ch.ethz.idsc.tensor.alg.PadRight;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.lie.TensorProduct;
import ch.ethz.idsc.tensor.mat.qr.QRDecomposition;
import ch.ethz.idsc.tensor.mat.qr.QRMathematica;
import ch.ethz.idsc.tensor.mat.qr.QRSignOperators;
import ch.ethz.idsc.tensor.sca.Sign;

/** API inspired by Mathematica convention:
 * The matrices that are input to the functions in Orthogonalize are interpreted as list of vectors
 * <pre>
 * matrix = { v0, v1, v2 }
 * </pre>
 * 
 * So that an orthogonal output would be for instance
 * <pre>
 * Orthogonalize[matrix] = { v0/|v0|, v1/|v1| - v0*(v0.v1)/|v0||v1|, ... }
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Orthogonalize.html">Orthogonalize</a>
 * 
 * @see OrthogonalMatrixQ
 * @see UnitaryMatrixQ */
public enum Orthogonalize {
  ;
  /** preserves the successive subspaces of the sequence of vectors in the order given in matrix
   * For instance, if matrix = {v0, ...} then Orthogonalize[matrix] = { v0/|v0|, ... }.
   * 
   * @param matrix of dimensions n x m
   * @return matrix of dimensions n x m with pairwise orthogonal row vectors
   * @throws Exception if given matrix is not a tensor of rank 2 */
  public static Tensor of(Tensor matrix) {
    QRDecomposition qrDecomposition = //
        QRDecomposition.of(ConjugateTranspose.of(matrix), QRSignOperators.ORIENTATION);
    return PadRight.zeros(Dimensions.of(matrix)).apply(QRMathematica.wrap(qrDecomposition).getInverseQ());
  }

  /***************************************************/
  /** Least square orthogonal fit to given matrix
   * 
   * for input of square matrix, the function returns a matrix with determinant +1
   * 
   * Reference:
   * "Least-Squares Rigid Motion Using SVD"
   * Olga Sorkine-Hornung and Michael Rabinovich, 2016
   * 
   * @param matrix of size k x n with k <= n
   * @return matrix of size k x n that satisfies {@link OrthogonalMatrixQ} */
  public static Tensor usingSvd(Tensor matrix) {
    int k = matrix.length();
    int n = Unprotect.dimension1Hint(matrix);
    if (k < n) {
      SingularValueDecomposition svd = SingularValueDecomposition.of(Transpose.of(matrix));
      return MatrixDotTranspose.of(svd.getV(), svd.getU());
    }
    if (n < k) // case is forbidden to avoid confusion (despite functionally permissive)
      throw TensorRuntimeException.of(matrix);
    SingularValueDecomposition svd = SingularValueDecomposition.of(matrix);
    Tensor rotation = MatrixDotTranspose.of(svd.getU(), svd.getV());
    if (Sign.isPositiveOrZero(Det.of(rotation)))
      return rotation;
    Tensor ve = svd.getV().get(Tensor.ALL, n - 1).negate();
    return rotation.add(TensorProduct.of(svd.getU().get(Tensor.ALL, n - 1), ve.add(ve)));
  }

  /** Least square orthogonal fit to given matrix
   * 
   * expression appears in geomstats - stiefel.py for the creation of uniform distributed
   * random samples of orthogonal frames in the stiefel manifold St(n, k).
   * Subsequently, the function was used to reproject affine combinations of orthogonal matrices
   * back to the Lie group o(n), and so(n)
   * 
   * @param matrix of size k x n with k <= n
   * @return matrix of size k x n that satisfies {@link OrthogonalMatrixQ}
   * @throws Exception if given matrix does not have maximal rank k */
  public static Tensor usingPD(Tensor matrix) {
    return PolarDecomposition.of(matrix).getR();
  }

  /***************************************************/
  /** EXPERIMENTAL
   * 
   * for input of square matrix, the function returns a matrix with determinant +1
   * 
   * @param matrix of size n x m with m <= n
   * @return matrix of size n x m of which the transpose satisfies {@link OrthogonalMatrixQ} */
  public static Tensor unprotected(Tensor matrix) {
    int n = matrix.length();
    SingularValueDecomposition svd = SingularValueDecomposition.of(matrix);
    Tensor result = MatrixDotTranspose.of(svd.getU(), svd.getV());
    if (n == Unprotect.dimension1Hint(matrix) && Sign.isNegative(Det.of(result))) {
      Tensor ue = svd.getU().get(Tensor.ALL, n - 1).negate();
      return result.add(TensorProduct.of(ue.add(ue), svd.getV().get(Tensor.ALL, n - 1)));
    }
    return result;
  }
}
