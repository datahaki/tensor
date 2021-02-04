// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.Sin;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/TukeyWindow.html">TukeyWindow</a> */
public class TukeyWindow extends ParameterizedWindow {
  private static final long serialVersionUID = 5432885331481060986L;
  public static final ScalarUnaryOperator FUNCTION = of(RationalScalar.of(1, 3));

  /** @param alpha
   * @return */
  public static ScalarUnaryOperator of(Scalar alpha) {
    return new TukeyWindow(alpha);
  }

  /***************************************************/
  private final Scalar a2;
  private final Scalar pi_a;

  private TukeyWindow(Scalar alpha) {
    super(alpha);
    a2 = alpha.multiply(RationalScalar.HALF);
    pi_a = Pi.VALUE.divide(alpha);
  }

  @Override // from ParameterizedWindow
  protected Scalar evaluate(Scalar x) {
    x = Abs.FUNCTION.apply(x);
    return Scalars.lessEquals(x, a2) //
        ? RealScalar.ONE
        : RationalScalar.HALF.add(RationalScalar.HALF.multiply(Sin.FUNCTION.apply(x.multiply(pi_a))));
  }
}
