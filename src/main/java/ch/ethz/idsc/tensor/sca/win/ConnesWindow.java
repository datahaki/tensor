// code by jph
package ch.ethz.idsc.tensor.sca.win;

import java.util.Objects;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/ConnesWindow.html">ConnesWindow</a> */
public class ConnesWindow extends ParameterizedWindow {
  private static final long serialVersionUID = -641958409505589259L;
  private static final Scalar N8 = RealScalar.of(-8);
  private static final Scalar _16 = RealScalar.of(16);
  public static final ScalarUnaryOperator FUNCTION = of(RealScalar.ONE);

  /** @param alpha typically greater than or equal to 1
   * @return */
  public static ScalarUnaryOperator of(Scalar alpha) {
    return new ConnesWindow(Objects.requireNonNull(alpha));
  }

  /***************************************************/
  private ConnesWindow(Scalar alpha) {
    super(alpha);
  }

  @Override
  protected Scalar evaluate(Scalar x) {
    Scalar x_a = x.divide(alpha);
    Scalar xa2 = x_a.multiply(x_a);
    return RealScalar.ONE.add(xa2.multiply(N8)).add(xa2.multiply(xa2).multiply(_16));
  }
}
