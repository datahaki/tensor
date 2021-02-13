// code by jph
package ch.ethz.idsc.tensor.nrm;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.api.TensorScalarFunction;
import ch.ethz.idsc.tensor.mat.SingularValueDecomposition;

/** Quote from Wikipedia: "The Schatten p-norms arise when applying the p-norm to the
 * vector of singular values of a matrix.
 * p == 1 yields the nuclear norm (also known as the trace norm, or the Ky Fan'n'-norm)." */
public class SchattenNorm implements TensorScalarFunction {
  private static final long serialVersionUID = 7474903862950425107L;

  /** Hint: for enhanced precision, use p as instance of {@link RationalScalar} if possible
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

  /***************************************************/
  private final Scalar p;
  private final TensorScalarFunction vectorNorm;

  private SchattenNorm(Scalar p) {
    this.p = p;
    vectorNorm = VectorNorm.of(p);
  }

  @Override
  public Scalar apply(Tensor matrix) {
    return vectorNorm.apply(SingularValueDecomposition.of(Unprotect.dimension1(matrix) <= matrix.length() //
        ? matrix
        : Transpose.of(matrix)).values());
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s]", getClass().getSimpleName(), p);
  }
}
