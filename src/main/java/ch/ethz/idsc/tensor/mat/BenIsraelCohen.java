// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.DeterminateScalarQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.nrm.Matrix2Norm;
import ch.ethz.idsc.tensor.sca.N;

/** Reference: Pseudo Inverse Wikipedia
 * 
 * our experiments suggest that the iterative method works well for matrices
 * with non-zero imaginary part. */
/* package */ class BenIsraelCohen {
  private static final int MAX_ITERATIONS = 128;

  /** @param matrix
   * @return pseudo inverse of given matrix */
  public static Tensor of(Tensor matrix) {
    return new BenIsraelCohen(matrix).pseudoInverse();
  }

  /***************************************************/
  private final Tensor matrix;

  private BenIsraelCohen(Tensor matrix) {
    this.matrix = matrix;
  }

  public Tensor pseudoInverse() {
    Scalar sigma = N.DOUBLE.apply(Matrix2Norm.bound(matrix.map(Unprotect::withoutUnit)));
    DeterminateScalarQ.require(sigma); // fail fast
    Scalar sigma2 = sigma.multiply(sigma);
    Tensor ai = ConjugateTranspose.of(matrix.map(UnitNegate.FUNCTION));
    if (Scalars.isZero(sigma2))
      return ai;
    ai = ai.divide(sigma2);
    for (int count = 0; count < MAX_ITERATIONS; ++count)
      if (Tolerance.CHOP.isClose(ai, ai = refine(ai)))
        return ai;
    throw TensorRuntimeException.of(matrix);
  }

  /** @param ai matrix that approximates the pseudo inverse of given matrix
   * @return refined approximate to the pseudo inverse of given matrix */
  private Tensor refine(Tensor ai) {
    Tensor dots = ai.length() <= matrix.length() //
        ? ai.dot(matrix).dot(ai)
        : ai.dot(matrix.dot(ai));
    return ai.subtract(dots).add(ai);
  }
}
