// code by jph
package ch.alpine.tensor.sca.win;

import java.util.Objects;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.bes.BesselI;
import ch.alpine.tensor.sca.pow.Sqrt;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/KaiserWindow.html">KaiserWindow</a> */
public class KaiserWindow extends ParameterizedWindow {
  public static final ScalarUnaryOperator FUNCTION = of(RealScalar.of(3));

  /** @param alpha typically greater than or equal to 1
   * @return */
  public static ScalarUnaryOperator of(Scalar alpha) {
    return new KaiserWindow(Objects.requireNonNull(alpha));
  }

  /** @param alpha
   * @return */
  public static ScalarUnaryOperator of(Number alpha) {
    return of(RealScalar.of(alpha));
  }

  // ---
  private final Scalar den;

  private KaiserWindow(Scalar alpha) {
    super(alpha);
    den = BesselI._0(alpha);
  }

  @Override // from ParameterizedWindow
  protected Scalar evaluate(Scalar x) {
    Scalar xx = x.add(x);
    Scalar t1 = Sqrt.FUNCTION.apply(RealScalar.ONE.subtract(xx));
    Scalar t2 = Sqrt.FUNCTION.apply(RealScalar.ONE.add(xx));
    return BesselI._0(Times.of(t1, t2, alpha)).divide(den);
  }

  @Override // from ParameterizedWindow
  protected String title() {
    return "KaiserWindow";
  }
}
