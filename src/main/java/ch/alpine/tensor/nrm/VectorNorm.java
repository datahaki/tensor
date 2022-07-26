// code by jph
package ch.alpine.tensor.nrm;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.pow.Power;

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

  // ---
  private final ScalarUnaryOperator p_power;
  private final Scalar p;
  private final Scalar p_reciprocal;

  private VectorNorm(Scalar p) {
    if (Scalars.lessThan(p, RealScalar.ONE))
      throw new Throw(p);
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
        .reduce(Scalar::add) //
        .orElseThrow(), //
        p_reciprocal);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("VectorNorm", p);
  }
}
