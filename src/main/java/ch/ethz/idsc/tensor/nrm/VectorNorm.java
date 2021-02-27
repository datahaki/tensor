// code by jph
package ch.ethz.idsc.tensor.nrm;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.api.TensorScalarFunction;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.Power;

/** p-Norm for vectors
 * 
 * implementation consistent with Mathematica
 * 
 * Important: For the special cases
 * <ul>
 * <li>p==1 identical to {@link Vector1Norm}
 * <li>p==2 identical to {@link Vector2Norm}
 * <li>p==Infinity identical to {@link VectorInfinityNorm}
 * </ul> */
public class VectorNorm implements TensorScalarFunction {
  private static final long serialVersionUID = -913697110648849886L;

  /** Hint: for enhanced precision, use p as instance of {@link RationalScalar} if possible
   * 
   * @param p exponent greater or equals 1
   * @return
   * @throws Exception if p is less than 1 */
  public static TensorScalarFunction of(Scalar p) {
    return new VectorNorm(p);
  }

  /** @param p exponent greater or equals 1
   * @return
   * @throws Exception if p is less than 1 */
  public static TensorScalarFunction of(Number p) {
    return of(RealScalar.of(p));
  }

  /***************************************************/
  private final ScalarUnaryOperator p_power;
  private final Scalar p;
  private final Scalar p_reciprocal;

  private VectorNorm(Scalar p) {
    if (Scalars.lessThan(p, RealScalar.ONE))
      throw TensorRuntimeException.of(p);
    p_power = Power.function(p);
    this.p = p;
    p_reciprocal = p.reciprocal();
  }

  @Override
  public Scalar apply(Tensor vector) {
    return Power.of(vector.stream() //
        .map(Scalar.class::cast) //
        .map(Abs.FUNCTION) //
        .map(p_power) //
        .reduce(Scalar::add).get(), //
        p_reciprocal);
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s]", getClass().getSimpleName(), p);
  }
}
