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
 * p == 1 yields the nuclear norm (also known as the trace norm, or the Ky Fan'n'-norm)."
 * p == 2 yields the {@link FrobeniusNorm}
 * 
 * @see Matrix1Norm
 * @see Matrix2Norm
 * @see MatrixInfinityNorm */
public class SchattenNorm implements TensorScalarFunction {
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
  private final TensorScalarFunction tensorScalarFunction;

  private SchattenNorm(Scalar p) {
    this.p = p;
    tensorScalarFunction = VectorNorm.of(p);
  }

  @Override
  public Scalar apply(Tensor matrix) {
    int n = matrix.length();
    int m = Unprotect.dimension1(matrix);
    SingularValueDecomposition svd = SingularValueDecomposition.of(m <= n //
        ? matrix
        : Transpose.of(matrix));
    return tensorScalarFunction.apply(svd.values());
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s]", getClass().getSimpleName(), p);
  }
}
