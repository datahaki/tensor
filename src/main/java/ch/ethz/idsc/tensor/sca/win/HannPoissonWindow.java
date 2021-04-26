// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Exp;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/HannPoissonWindow.html">HannPoissonWindow</a> */
public class HannPoissonWindow extends ParameterizedWindow {
  public static final ScalarUnaryOperator FUNCTION = of(RealScalar.ONE);

  /** @param alpha
   * @return */
  public static ScalarUnaryOperator of(Scalar alpha) {
    return new HannPoissonWindow(alpha);
  }

  /***************************************************/
  private final Scalar a2;

  private HannPoissonWindow(Scalar alpha) {
    super(alpha);
    a2 = alpha.add(alpha);
  }

  @Override // from ParameterizedWindow
  protected Scalar evaluate(Scalar x) {
    Scalar den = Exp.FUNCTION.apply(a2.multiply(Abs.FUNCTION.apply(x)));
    return RealScalar.ONE.add(Cos.FUNCTION.apply(Pi.TWO.multiply(x))).divide(den.add(den));
  }
}
