// code by jph
package ch.ethz.idsc.tensor.sca;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Surd.html">Surd</a> */
public class Surd implements ScalarUnaryOperator {
  /** @param exponent non-zero
   * @return
   * @throws Exception if given exponent is zero */
  public static ScalarUnaryOperator of(long exponent) {
    return new Surd(Power.function(RationalScalar.of(1, exponent)));
  }

  /***************************************************/
  private final ScalarUnaryOperator power;

  private Surd(ScalarUnaryOperator power) {
    this.power = power;
  }

  @Override
  public Scalar apply(Scalar scalar) {
    return Sign.isPositiveOrZero(scalar) //
        ? power.apply(scalar)
        : power.apply(scalar.negate()).negate();
  }
}
