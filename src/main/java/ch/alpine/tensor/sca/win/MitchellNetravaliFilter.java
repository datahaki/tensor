// code by jph
package ch.alpine.tensor.sca.win;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.itp.MitchellNetravaliKernel;

/** not interpolatory */
/* package */ class MitchellNetravaliFilter extends ParameterizedWindow {
  private static final Scalar _4 = RealScalar.of(4);
  public static final ScalarUnaryOperator FUNCTION = of(Rational.THIRD);

  /** @param c
   * @return */
  public static ScalarUnaryOperator of(Scalar c) {
    return new MitchellNetravaliFilter(c);
  }

  /** @param c
   * @return */
  public static ScalarUnaryOperator of(Number c) {
    return of(RealScalar.of(c));
  }

  // ---
  private final ScalarUnaryOperator scalarUnaryOperator;

  private MitchellNetravaliFilter(Scalar c) {
    super(c);
    scalarUnaryOperator = MitchellNetravaliKernel.of(RealScalar.ONE.subtract(c.add(c)), c);
  }

  @Override // from ParameterizedWindow
  protected Scalar evaluate(Scalar x) {
    return scalarUnaryOperator.apply(_4.multiply(x));
  }

  @Override // from ParameterizedWindow
  protected String title() {
    return "MitchellNetravaliFilter";
  }
}
