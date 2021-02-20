// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.Tensor;
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

  /** expression appears in geomstats - stiefel.py for the creation of uniform distributed
   * random samples of orthogonal frames in the stiefel manifold St(n, k).
   * Subsequently, the function was used to reproject affine combinations of orthogonal matrices
   * back to the Lie group o(n), and so(n)
   * 
   * @param matrix of size k x n with k <= n
   * @return matrix of size k x n that satisfies {@link OrthogonalMatrixQ} */
  public static Tensor usingPD(Tensor matrix) {
    return PolarDecomposition.of(matrix).getR();
    // PolarDecomposition polarDecomposition = PolarDecomposition.of(matrix);
    // Tensor rotation = polarDecomposition.getR();
    // if (Integers.isEven(rotation.length()) && //
    // SquareMatrixQ.of(rotation) && //
    // Sign.isNegative(Det.of(rotation))) {
    // System.out.println("FLIP PD because EVEN");
    // rotation.set(Tensor::negate, rotation.length() - 1);
    // }
    // return rotation;
  }

  /** for input of square matrix, the function returns a matrix with determinant +1
   * 
   * @param matrix of size k x n with k <= n
   * @return matrix of size k x n that satisfies {@link OrthogonalMatrixQ} */
  public static Tensor usingSvd(Tensor matrix) {
    SingularValueDecomposition svd = SingularValueDecomposition.of(Transpose.of(matrix));
    Tensor rotation = MatrixDotTranspose.of(svd.getV(), svd.getU());
    if (Sign.isNegative(Det.of(rotation))) {
      int last = matrix.length() - 1;
      Tensor ue = svd.getU().get(Tensor.ALL, last).negate();
      return rotation.add(TensorProduct.of(svd.getV().get(Tensor.ALL, last), ue.add(ue)));
    }
    return rotation;
  }
}
