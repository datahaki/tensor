// code by jph
package ch.alpine.tensor.sca.win;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/HannWindow.html">HannWindow</a> */
public class HannWindow extends ParameterizedWindow {
  public static final ScalarUnaryOperator FUNCTION = of(Rational.HALF);

  /** @param alpha
   * @return */
  public static ScalarUnaryOperator of(Scalar alpha) {
    return new HannWindow(alpha);
  }

  // ---
  private final Scalar a1;

  private HannWindow(Scalar alpha) {
    super(alpha);
    a1 = RealScalar.ONE.subtract(alpha);
  }

  @Override // from ParameterizedWindow
  protected Scalar evaluate(Scalar x) {
    return StaticHelper.deg1(alpha, a1, x);
  }

  @Override // from ParameterizedWindow
  protected String title() {
    return "HannWindow";
  }
}
