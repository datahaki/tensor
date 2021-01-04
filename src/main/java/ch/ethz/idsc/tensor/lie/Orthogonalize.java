// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.PadRight;
import ch.ethz.idsc.tensor.mat.ConjugateTranspose;
import ch.ethz.idsc.tensor.mat.OrthogonalMatrixQ;
import ch.ethz.idsc.tensor.mat.UnitaryMatrixQ;

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
}
