// code by jph
package ch.alpine.tensor.mat.pi;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.nrm.Matrix2Norm;
import ch.alpine.tensor.sca.N;

/** implementation also operates on matrices with mixed units, for example:
 * {{-4/5[m^-2], 3/10[m^-1*rad^-1]}, {3/10[m^-1*rad^-1], -1/20[rad^-2]}}
 * 
 * Experiments suggest that the iterative method also works well for matrices
 * with complex entries, i.e. scalars of type {@link ComplexScalar}.
 * 
 * Reference: Pseudo Inverse Wikipedia */
/* package */ class BenIsraelCohen {
  /** @param matrix
   * @return pseudo inverse of given matrix */
  public static Tensor of(Tensor matrix) {
    return new BenIsraelCohen(matrix).pseudoInverse();
  }

  // ---
  private final Tensor matrix;

  private BenIsraelCohen(Tensor matrix) {
    this.matrix = matrix;
  }

  /** @return pseudo inverse of given matrix */
  public Tensor pseudoInverse() {
    Scalar sigma = N.DOUBLE.apply(Matrix2Norm.bound(matrix.map(Unprotect::withoutUnit)));
    FiniteScalarQ.require(sigma); // fail fast
    Scalar sigma2 = sigma.multiply(sigma);
    Tensor ai = ConjugateTranspose.of(matrix.map(Unprotect::negateUnit));
    if (Scalars.isZero(sigma2)) // special case that all entries of matrix are zero
      return ai;
    ai = ai.divide(sigma2);
    int max = PseudoInverse.MAX_ITERATIONS.get();
    for (int count = 0; count < max; ++count)
      if (Tolerance.CHOP.isClose(ai, ai = refine(ai)))
        return ai;
    throw new Throw(matrix);
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
