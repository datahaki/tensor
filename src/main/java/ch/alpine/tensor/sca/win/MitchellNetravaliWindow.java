// code by jph
package ch.alpine.tensor.sca.win;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.itp.MitchellNetravaliKernel;

/** not interpolatory !? */
public class MitchellNetravaliWindow extends ParameterizedWindow {
  private static final Scalar _4 = RealScalar.of(4);
  public static final ScalarUnaryOperator FUNCTION = of(RationalScalar.of(1, 3));

  /** @param c
   * @return */
  public static ScalarUnaryOperator of(Scalar c) {
    return new MitchellNetravaliWindow(c);
  }

  /** @param c
   * @return */
  public static ScalarUnaryOperator of(Number c) {
    return of(RealScalar.of(c));
  }

  /***************************************************/
  private final ScalarUnaryOperator scalarUnaryOperator;

  private MitchellNetravaliWindow(Scalar c) {
    super(c);
    scalarUnaryOperator = MitchellNetravaliKernel.of(RealScalar.ONE.subtract(c.add(c)), c);
  }

  @Override
  protected Scalar evaluate(Scalar x) {
    return scalarUnaryOperator.apply(_4.multiply(x));
  }
}
