// code by jph
package ch.ethz.idsc.tensor.sca.win;

import java.util.Objects;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sca.Exp;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/GaussianWindow.html">GaussianWindow</a> */
public class GaussianWindow extends ParameterizedWindow {
  private static final long serialVersionUID = -8116183206533972074L;
  private static final Scalar HALF_NEGATE = RationalScalar.HALF.negate();
  /** gaussian window with standard deviation of sigma 3/10,
   * which is the default in Mathematica and results in
   * GaussianWindow[1/2]=0.24935220877729616 */
  public static final ScalarUnaryOperator FUNCTION = of(RationalScalar.of(3, 10));

  /** @param sigma
   * @return */
  public static ScalarUnaryOperator of(Scalar sigma) {
    return new GaussianWindow(Objects.requireNonNull(sigma));
  }

  /***************************************************/
  private GaussianWindow(Scalar sigma) {
    super(sigma);
  }

  @Override
  protected Scalar evaluate(Scalar x) {
    Scalar ratio = x.divide(alpha);
    return Exp.FUNCTION.apply(ratio.multiply(ratio).multiply(HALF_NEGATE));
  }
}
