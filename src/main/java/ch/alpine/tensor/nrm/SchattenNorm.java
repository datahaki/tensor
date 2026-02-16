// code by jph
package ch.alpine.tensor.nrm;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.mat.sv.SingularValueList;

/** Quote from Wikipedia: "The Schatten p-norms arise when applying the p-norm to the
 * vector of singular values of a matrix.
 * p == 1 yields the nuclear norm (also known as the trace norm, or the Ky Fan'n'-norm)."
 * p == 2 yields the {@link FrobeniusNorm}
 * 
 * @see Matrix1Norm
 * @see Matrix2Norm
 * @see MatrixInfinityNorm */
public class SchattenNorm implements TensorScalarFunction {
  /** Hint: for enhanced precision, use p as instance of {@link Rational} if possible
   *
   * @param p exponent greater or equals 1
   * @return
   * @throws Exception if p is less than 1 */
  public static TensorScalarFunction of(Scalar p) {
    return new SchattenNorm(p);
  }

  /** @param p exponent greater or equals 1
   * @return
   * @throws Exception if p is less than 1 */
  public static TensorScalarFunction of(Number p) {
    return of(RealScalar.of(p));
  }

  // ---
  private final Scalar p;
  private final TensorScalarFunction tensorScalarFunction;

  private SchattenNorm(Scalar p) {
    this.p = p;
    tensorScalarFunction = VectorNorm.of(p);
  }

  @Override
  public Scalar apply(Tensor matrix) {
    return tensorScalarFunction.apply(SingularValueList.of(matrix));
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("SchattenNorm", p);
  }
}
