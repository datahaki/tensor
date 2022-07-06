// code by jph
package ch.alpine.tensor.sca.win;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.exp.Exp;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/PoissonWindow.html">PoissonWindow</a> */
public class PoissonWindow extends ParameterizedWindow {
  public static final ScalarUnaryOperator FUNCTION = of(RealScalar.of(3));

  /** @param alpha
   * @return */
  public static ScalarUnaryOperator of(Scalar alpha) {
    return new PoissonWindow(alpha);
  }

  // ---
  private final Scalar n2a;

  private PoissonWindow(Scalar alpha) {
    super(alpha);
    n2a = alpha.add(alpha).negate();
  }

  @Override // from ParameterizedWindow
  protected Scalar evaluate(Scalar x) {
    return Exp.FUNCTION.apply(Abs.FUNCTION.apply(x).multiply(n2a));
  }

  @Override // from ParameterizedWindow
  protected String title() {
    return "PoissonWindow";
  }
}
