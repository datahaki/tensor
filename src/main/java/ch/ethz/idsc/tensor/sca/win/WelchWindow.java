// code by jph
package ch.ethz.idsc.tensor.sca.win;

import java.util.Objects;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/WelchWindow.html">WelchWindow</a> */
public class WelchWindow extends ParameterizedWindow {
  private static final Scalar _4 = RealScalar.of(4);
  public static final ScalarUnaryOperator FUNCTION = of(RealScalar.ONE);

  /** @param alpha typically greater than or equal to 1
   * @return */
  public static ScalarUnaryOperator of(Scalar alpha) {
    return new WelchWindow(Objects.requireNonNull(alpha));
  }

  /***************************************************/
  private WelchWindow(Scalar alpha) {
    super(alpha);
  }

  @Override
  protected Scalar evaluate(Scalar x) {
    Scalar x_a = x.divide(alpha);
    return RealScalar.ONE.subtract(x_a.multiply(x_a).multiply(_4));
  }
}
