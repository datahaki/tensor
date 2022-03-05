// code by jph
package ch.alpine.tensor.sca.win;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.tri.Sin;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/TukeyWindow.html">TukeyWindow</a> */
public class TukeyWindow extends ParameterizedWindow {
  public static final ScalarUnaryOperator FUNCTION = of(RationalScalar.of(1, 3));

  /** @param alpha
   * @return */
  public static ScalarUnaryOperator of(Scalar alpha) {
    return new TukeyWindow(alpha);
  }

  // ---
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
