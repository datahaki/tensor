// code by jph
package ch.alpine.tensor.sca.var;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.sca.pow.Power;

/** Does not work properly with units?
 * 
 * <p>Reference:
 * "Interpolation on Scattered Data in Multidimensions" in NR, 2007 */
public class InversePowerVariogram implements ScalarUnaryOperator {
  /** @param exponent for instance 2
   * @return */
  public static ScalarUnaryOperator of(Scalar exponent) {
    if (exponent.equals(RealScalar.ZERO))
      return _ -> RealScalar.ONE;
    return new InversePowerVariogram(exponent.equals(RealScalar.ONE) //
        ? Scalar::reciprocal
        : Power.function(exponent.negate()));
  }

  /** @param exponent for instance 2
   * @return */
  public static ScalarUnaryOperator of(Number exponent) {
    return of(RealScalar.of(exponent));
  }

  // ---
  private final ScalarUnaryOperator power;

  private InversePowerVariogram(ScalarUnaryOperator power) {
    this.power = power;
  }

  @Override
  public Scalar apply(Scalar r) {
    return Scalars.isZero(r) //
        ? DoubleScalar.POSITIVE_INFINITY
        : power.apply(r);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("InversePowerVariogram", power);
  }
}
