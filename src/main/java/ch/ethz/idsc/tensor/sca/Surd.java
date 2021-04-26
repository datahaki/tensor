// code by jph
package ch.ethz.idsc.tensor.sca;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;

/** gives the real-valued n-th root of x
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Surd.html">Surd</a> */
public class Surd implements ScalarUnaryOperator {
  /** @param n non-zero
   * @return operator that gives the real-valued n-th root of x
   * @throws Exception if given exponent is zero */
  public static ScalarUnaryOperator of(long n) {
    return new Surd(n);
  }

  /***************************************************/
  private final long n;
  private final ScalarUnaryOperator power;

  private Surd(long n) {
    this.n = n;
    this.power = Power.function(RationalScalar.of(1, n));
  }

  @Override
  public Scalar apply(Scalar scalar) {
    return Sign.isPositiveOrZero(scalar) //
        ? power.apply(scalar)
        : power.apply(scalar.negate()).negate();
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%d]", getClass().getSimpleName(), n);
  }
}
