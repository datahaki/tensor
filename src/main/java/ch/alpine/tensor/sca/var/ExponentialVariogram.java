// code by jph
package ch.alpine.tensor.sca.var;

import java.util.Objects;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Exp;

/** <p>The input of the variogram has unit of a.
 * The output of the variogram has unit of b.
 * 
 * @param a positive
 * @param b */
public class ExponentialVariogram implements ScalarUnaryOperator {
  public static ScalarUnaryOperator of(Scalar a, Scalar b) {
    return Scalars.isZero(a) //
        ? ConstantOneVariogram.INSTANCE
        : new ExponentialVariogram(a, b);
  }

  /** @param a positive
   * @param b
   * @return */
  public static ScalarUnaryOperator of(Number a, Number b) {
    return of(RealScalar.of(a), RealScalar.of(b));
  }

  // ---
  private final Scalar a;
  private final Scalar b;

  private ExponentialVariogram(Scalar a, Scalar b) {
    this.a = Sign.requirePositive(a);
    this.b = Objects.requireNonNull(b);
  }

  @Override
  public Scalar apply(Scalar r) {
    Sign.requirePositiveOrZero(r);
    return RealScalar.ONE.subtract(Exp.FUNCTION.apply(r.divide(a).negate())).multiply(b);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("ExponentialVariogram", a, b);
  }
}
