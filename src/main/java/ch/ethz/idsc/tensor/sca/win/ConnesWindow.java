// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/ConnesWindow.html">ConnesWindow</a> */
public class ConnesWindow implements ScalarUnaryOperator {
  private static final long serialVersionUID = -6008384478066071885L;
  private static final Scalar _N8 = RealScalar.of(-8);
  private static final Scalar _16 = RealScalar.of(16);
  public static final ScalarUnaryOperator FUNCTION = of(RealScalar.ONE);

  /** @param alpha greater equals 1
   * @return */
  public static ScalarUnaryOperator of(Scalar alpha) {
    if (Scalars.lessEquals(RealScalar.ONE, alpha))
      return new ConnesWindow(alpha);
    throw TensorRuntimeException.of(alpha);
  }

  /***************************************************/
  private final Scalar alpha;

  private ConnesWindow(Scalar alpha) {
    this.alpha = alpha;
  }

  @Override
  public Scalar apply(Scalar x) {
    if (StaticHelper.SEMI.isInside(x)) {
      Scalar x_a = x.divide(alpha);
      Scalar xa2 = x_a.multiply(x_a);
      Scalar xa4 = xa2.multiply(xa2);
      return RealScalar.ONE.add(xa2.multiply(_N8)).add(xa4.multiply(_16));
    }
    return RealScalar.ZERO;
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s]", getClass().getSimpleName(), alpha);
  }
}
