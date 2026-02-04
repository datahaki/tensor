// code by jph
package ch.alpine.tensor.sca.var;

import java.util.Objects;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Exp;

/** <p>The input of the variogram has unit of a.
 * The output of the variogram has unit of b.
 * 
 * @param a positive
 * @param b */
public record ExponentialVariogram(Scalar a, Scalar b) implements ScalarUnaryOperator {
  /** @param a positive
   * @param b
   * @return */
  public static ScalarUnaryOperator of(Number a, Number b) {
    return new ExponentialVariogram(RealScalar.of(a), RealScalar.of(b));
  }

  // ---
  public ExponentialVariogram {
    Sign.requirePositive(a);
    Objects.requireNonNull(b);
  }

  @Override
  public Scalar apply(Scalar r) {
    return RealScalar.ONE.subtract(Exp.FUNCTION.apply(r.divide(a).negate())).multiply(b);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("ExponentialVariogram", a, b);
  }
}
