// code by jph
package ch.alpine.tensor.sca.win;

import java.util.Objects;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/CauchyWindow.html">CauchyWindow</a> */
public class CauchyWindow extends ParameterizedWindow {
  private static final Scalar _4 = RealScalar.of(4);
  public static final ScalarUnaryOperator FUNCTION = of(RealScalar.of(3));

  /** @param alpha
   * @return */
  public static ScalarUnaryOperator of(Scalar alpha) {
    return new CauchyWindow(Objects.requireNonNull(alpha));
  }

  // ---
  private CauchyWindow(Scalar alpha) {
    super(alpha);
  }

  @Override // from ParameterizedWindow
  protected Scalar evaluate(Scalar x) {
    Scalar x_a = x.multiply(alpha);
    return RealScalar.ONE.divide(RealScalar.ONE.add(x_a.multiply(x_a).multiply(_4)));
  }

  @Override // from ParameterizedWindow
  protected String title() {
    return "CauchyWindow";
  }
}
