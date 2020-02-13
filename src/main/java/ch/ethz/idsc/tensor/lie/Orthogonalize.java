// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.PadRight;
import ch.ethz.idsc.tensor.mat.ConjugateTranspose;
import ch.ethz.idsc.tensor.mat.OrthogonalMatrixQ;
import ch.ethz.idsc.tensor.mat.UnitaryMatrixQ;

/** Implementation is consistent with Mathematica
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Orthogonalize.html">Orthogonalize</a>
 * 
 * @see OrthogonalMatrixQ
 * @see UnitaryMatrixQ */
public enum Orthogonalize {
  ;
  /** @param matrix
   * @return matrix with pairwise orthogonal row vectors with the same span as input vectors */
  public static Tensor of(Tensor matrix) {
    Tensor tensor = QRMathematica.wrap(QRDecomposition.of(ConjugateTranspose.of(matrix))).getInverseQ();
    return PadRight.zeros(Dimensions.of(matrix)).apply(tensor);
  }
}
