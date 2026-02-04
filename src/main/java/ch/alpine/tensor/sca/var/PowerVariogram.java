// code by jph
package ch.alpine.tensor.sca.var;

import java.util.Objects;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.pow.Power;

/** Does not work properly with units?
 * 
 * Unbounded. Unbounded variograms occur when data has a global trend.
 * 
 * <p>Reference:
 * "Interpolation on Scattered Data in Multidimensions" in NR, 2007 */
public record PowerVariogram(Scalar alpha, ScalarUnaryOperator power) implements ScalarUnaryOperator {
  /** Quote:
   * "A good general choice is exponent=3/2, but for functions with a strong linear trend,
   * you may want to experiment with values as large as 1.99."
   * 
   * @param alpha "is fitted by unweighted least squares over all pairs of data points i and j"
   * @param exponent in the range [1, 2) "The value 2 gives a degenerate matrix and meaningless results."
   * @return */
  public static PowerVariogram of(Scalar alpha, Scalar exponent) {
    return new PowerVariogram(Objects.requireNonNull(alpha), Power.function(exponent));
  }

  /** @param alpha
   * @param exponent
   * @return */
  public static PowerVariogram of(Number alpha, Number exponent) {
    return of(RealScalar.of(alpha), RealScalar.of(exponent));
  }

  @Override
  public Scalar apply(Scalar r) {
    Sign.requirePositiveOrZero(r);
    return power.apply(r.divide(alpha));
  }
}
