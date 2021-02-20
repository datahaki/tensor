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
import ch.ethz.idsc.tensor.sca.Sign;

/** Implementation is consistent with Mathematica:
 * "If some of the input vectors are not linearly independent, the output will contain zero vectors."
 * "All nonzero vectors in the output are normalized to unit length."
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Orthogonalize.html">Orthogonalize</a>
 * 
 * @see OrthogonalMatrixQ
 * @see UnitaryMatrixQ */
public enum Orthogonalize {
  ;
  /** If all of the input vectors are linearly independent, the matrix returned
   * satisfies the predicate {@link OrthogonalMatrixQ}.
   * 
   * @param matrix of dimensions n x m
   * @return matrix of dimensions n x m with pairwise orthogonal row vectors
   * with the same span as the rows of the input matrix
   * @throws Exception if given matrix is not a tensor of rank 2 */
  public static Tensor of(Tensor matrix) {
    QRDecomposition qrDecomposition = //
        QRDecomposition.of(ConjugateTranspose.of(matrix), QRSignOperators.ORIENTATION);
    return PadRight.zeros(Dimensions.of(matrix)).apply(QRMathematica.wrap(qrDecomposition).getInverseQ());
  }

  /** for input of square matrix, the function returns a matrix with determinant +1
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
    Tensor ve = svd.getV().get(Tensor.ALL, k - 1).negate();
    return rotation.add(TensorProduct.of(svd.getU().get(Tensor.ALL, k - 1), ve.add(ve)));
  }

  /** expression appears in geomstats - stiefel.py for the creation of uniform distributed
   * random samples of orthogonal frames in the stiefel manifold St(n, k).
   * Subsequently, the function was used to reproject affine combinations of orthogonal matrices
   * back to the Lie group o(n), and so(n)
   * 
   * @param matrix of size k x n with k <= n
   * @return matrix of size k x n that satisfies {@link OrthogonalMatrixQ} */
  public static Tensor usingPD(Tensor matrix) {
    return PolarDecomposition.of(matrix).getR();
  }
}
