// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/HannWindow.html">HannWindow</a> */
public class HannWindow extends ParameterizedWindow {
  private static final long serialVersionUID = 7913793267394141590L;
  public static final ScalarUnaryOperator FUNCTION = of(RationalScalar.HALF);

  /** @param alpha
   * @return */
  public static ScalarUnaryOperator of(Scalar alpha) {
    return new HannWindow(alpha);
  }

  /***************************************************/
  private final Scalar a1;

  private HannWindow(Scalar alpha) {
    super(alpha);
    a1 = RealScalar.ONE.subtract(alpha);
  }

  @Override
  protected Scalar evaluate(Scalar x) {
    return StaticHelper.deg1(alpha, a1, x);
  }
}
