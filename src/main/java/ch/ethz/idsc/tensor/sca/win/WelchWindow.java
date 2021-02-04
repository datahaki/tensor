// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/WelchWindow.html">WelchWindow</a> */
public class WelchWindow implements ScalarUnaryOperator {
  private static final long serialVersionUID = 3862546958843635679L;
  private static final Scalar _4 = RealScalar.of(4);
  public static final ScalarUnaryOperator FUNCTION = of(RealScalar.ONE);

  /** @param alpha greater equals 1
   * @return */
  public static ScalarUnaryOperator of(Scalar alpha) {
    if (Scalars.lessEquals(RealScalar.ONE, alpha))
      return new WelchWindow(alpha);
    throw TensorRuntimeException.of(alpha);
  }

  /***************************************************/
  private final Scalar alpha;

  private WelchWindow(Scalar alpha) {
    this.alpha = alpha;
  }

  @Override
  public Scalar apply(Scalar x) {
    if (StaticHelper.SEMI.isInside(x)) {
      Scalar x_a = x.divide(alpha);
      return RealScalar.ONE.subtract(x_a.multiply(x_a).multiply(_4));
    }
    return RealScalar.ZERO;
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s]", getClass().getSimpleName(), alpha);
  }
}
