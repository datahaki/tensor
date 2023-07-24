// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** Remark: implementation is not consistent with Mathematica
 * for complex numbers.
 * 
 * For any scalar:
 * Numerator[scalar] / Denominator[scalar] == scalar
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Numerator.html">Numerator</a>
 * 
 * @see Denominator */
public enum Numerator implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    return scalar instanceof RationalScalar rationalScalar //
        ? RealScalar.of(rationalScalar.numerator())
        : scalar;
  }

  /** @param scalar
   * @return
   * @throws Exception */
  public static int intValueExact(Scalar scalar) {
    return Scalars.intValueExact(FUNCTION.apply(scalar));
  }

  /** @param scalar
   * @return
   * @throws Exception */
  public static long longValueExact(Scalar scalar) {
    return Scalars.longValueExact(FUNCTION.apply(scalar));
  }
}
