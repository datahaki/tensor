// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.nrm.MatrixNorm2;
import ch.ethz.idsc.tensor.sca.Chop;

/** Reference: Pseudo Inverse Wikipedia
 * 
 * our experiments suggest that the iterative method works well for matrices
 * with non-zero imaginary part.
 * 
 * our experiments suggest that the method does not help to refine the pseudo
 * inverse obtained via svd since the threshold of when to truncate a singular
 * value to zero (or to invert) leads to significant numerical deviation of
 * order 1E13. */
/* package */ class BenIsraelCohen {
  private final Tensor matrix;

  /** @param matrix with rows => cols */
  public BenIsraelCohen(Tensor matrix) {
    this.matrix = matrix;
  }

  /** @param chop
   * @param max
   * @return pseudoinverse of given matrix */
  public Tensor of(Chop chop, int max) {
    Scalar sigma = MatrixNorm2.bound(matrix);
    Tensor ai = ConjugateTranspose.of(matrix.divide(sigma.multiply(sigma)));
    for (int count = 0; count < max; ++count)
      if (chop.isClose(ai, ai = refine(ai)))
        return ai;
    throw TensorRuntimeException.of(matrix);
  }

  /** @param ai matrix that approximates the pseudo inverse of given matrix
   * @return */
  private Tensor refine(Tensor ai) {
    return ai.subtract(ai.dot(matrix).dot(ai)).add(ai);
  }
}
