// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.Exp;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/PoissonWindow.html">PoissonWindow</a> */
public class PoissonWindow extends ParameterizedWindow {
  public static final ScalarUnaryOperator FUNCTION = of(RealScalar.of(3));

  /** @param alpha
   * @return */
  public static ScalarUnaryOperator of(Scalar alpha) {
    return new PoissonWindow(alpha);
  }

  /***************************************************/
  private final Scalar n2a;

  private PoissonWindow(Scalar alpha) {
    super(alpha);
    n2a = alpha.add(alpha).negate();
  }

  @Override // from ParameterizedWindow
  protected Scalar evaluate(Scalar x) {
    return Exp.FUNCTION.apply(Abs.FUNCTION.apply(x).multiply(n2a));
  }
}
