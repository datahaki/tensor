// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Power;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/CosineWindow.html">CosineWindow</a> */
public class CosineWindow extends ParameterizedWindow {
  private static final long serialVersionUID = 608831662955447648L;
  public static final ScalarUnaryOperator FUNCTION = of(RealScalar.ONE);

  /** @param alpha
   * @return */
  public static ScalarUnaryOperator of(Scalar alpha) {
    return new CosineWindow(alpha);
  }

  /***************************************************/
  private final ScalarUnaryOperator power;

  private CosineWindow(Scalar alpha) {
    super(alpha);
    power = Power.function(alpha);
  }

  @Override // from ParameterizedWindow
  protected Scalar evaluate(Scalar x) {
    return power.apply(Cos.FUNCTION.apply(x.multiply(Pi.VALUE)));
  }
}
