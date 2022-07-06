// code by jph
package ch.alpine.tensor.sca.pow;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.sca.Sign;

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

  // ---
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
    return MathematicaFormat.of("Surd", n);
  }
}
