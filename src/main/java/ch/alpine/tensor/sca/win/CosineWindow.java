// code by jph
package ch.alpine.tensor.sca.win;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.tri.Cos;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/CosineWindow.html">CosineWindow</a> */
public class CosineWindow extends ParameterizedWindow {
  public static final ScalarUnaryOperator FUNCTION = of(RealScalar.ONE);

  /** @param alpha
   * @return */
  public static ScalarUnaryOperator of(Scalar alpha) {
    return new CosineWindow(alpha);
  }

  // ---
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
