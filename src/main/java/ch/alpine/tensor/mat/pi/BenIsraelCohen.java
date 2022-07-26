// code by jph
package ch.alpine.tensor.mat.pi;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.nrm.Matrix2Norm;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.InvertUnlessZero;
import ch.alpine.tensor.sca.N;

/** implementation also operates on matrices with mixed units, for example:
 * {{-4/5[m^-2], 3/10[m^-1*rad^-1]}, {3/10[m^-1*rad^-1], -1/20[rad^-2]}}
 * 
 * Experiments suggest that the iterative method also works well for matrices
 * with complex entries, i.e. scalars of type {@link ComplexScalar}.
 * 
 * Reference: Pseudo Inverse Wikipedia */
/* package */ class BenIsraelCohen {
  private static final int MAX_ITERATIONS = 128;

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
    Tensor ai = ConjugateTranspose.of(matrix.map(UnitNegate.FUNCTION));
    if (Scalars.isZero(sigma2)) // special case that all entries of matrix are zero
      return ai;
    ai = ai.divide(sigma2);
    for (int count = 0; count < MAX_ITERATIONS; ++count)
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

  /** UnitNegate also appears in {@link InvertUnlessZero}
   * yet we do not place the function in the global scope */
  private enum UnitNegate implements ScalarUnaryOperator {
    FUNCTION;

    @Override
    public Scalar apply(Scalar scalar) {
      if (scalar instanceof Quantity) {
        Quantity quantity = (Quantity) scalar;
        return Quantity.of(quantity.value(), quantity.unit().negate());
      }
      return scalar;
    }
  }
}
