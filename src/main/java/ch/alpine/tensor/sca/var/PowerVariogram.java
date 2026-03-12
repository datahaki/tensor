// code by jph
package ch.alpine.tensor.sca.var;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.pow.Power;

/** Does not work properly with units?
 * 
 * <p>Reference:
 * "Interpolation on Scattered Data in Multidimensions" in NR, 2007 */
public class PowerVariogram implements ScalarUnaryOperator {
  /** @param exponent for instance 2
   * @return */
  public static ScalarUnaryOperator of(Scalar exponent) {
    return new PowerVariogram(exponent);
  }

  /** @param exponent for instance 2
   * @return */
  public static ScalarUnaryOperator of(Number exponent) {
    return of(RealScalar.of(exponent));
  }

  // ---
  private final Scalar exponent;
  private final ScalarUnaryOperator power;

  private PowerVariogram(Scalar exponent) {
    this.exponent = exponent;
    this.power = Power.function(exponent);
  }

  @Override
  public Scalar apply(Scalar r) {
    Sign.requirePositiveOrZero(r);
    return power.apply(r);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("PowerVariogram", exponent);
  }
}
