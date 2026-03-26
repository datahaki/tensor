// code by jph
package ch.alpine.tensor.sca.pow;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** Power exponentiates a given scalar by an exponent.
 * The scalar type is required to implement {@link PowerInterface}
 * in order for the operation to succeed.
 * 
 * <p>The implementation is compliant to the Java convention:
 * java.lang.Math.pow(0, 0) == 1
 * 
 * <p>not compliant with Mathematica
 * Mathematica::Power[0, 0] == 0^0 -> indeterminate
 * 
 * <pre>
 * Power[NaN, *] == NaN
 * Power[*, NaN] == NaN
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Power.html">Power</a> */
public enum Power {
  ;
  /** function attempts to give power as accurately as possible
   * and ultimately makes use of the identity
   * <code>x^y == Exp(y * Log(x))</code>
   * 
   * @param scalar
   * @param exponent
   * @return scalar ^ exponent */
  public static Scalar of(Scalar scalar, Scalar exponent) {
    return evaluate(scalar, exponent);
  }

  /** @param scalar
   * @param exponent
   * @return scalar ^ exponent */
  public static Scalar of(Scalar scalar, Number exponent) {
    return of(scalar, RealScalar.of(exponent));
  }

  /** @param number
   * @param exponent
   * @return number ^ exponent */
  public static Scalar of(Number number, Scalar exponent) {
    return of(RealScalar.of(number), exponent);
  }

  /** @param number
   * @param exponent
   * @return number ^ exponent */
  public static Scalar of(Number number, Number exponent) {
    return of(RealScalar.of(number), RealScalar.of(exponent));
  }

  /** @param exponent
   * @return function that maps a scalar to scalar ^ exponent */
  public static ScalarUnaryOperator function(Scalar exponent) {
    // if (exponent instanceof Rational rational) {
    // BigInteger num = rational.numerator();
    // BigInteger den = rational.denominator();
    // if (den.equals(BigInteger.ONE)) {
    // if (num.equals(BigInteger.ONE))
    // return s -> s;
    // if (num.equals(BigInteger.ONE.negate()))
    // return s -> s.reciprocal();
    // if (num.equals(BigInteger.TWO))
    // return s -> s.multiply(s);
    // if (num.equals(BigInteger.TWO.negate()))
    // return s -> s.multiply(s).reciprocal();
    // }
    // // TODO TENSOR exponents of the form 1/4, etc. could also be valid
    // if (den.equals(BigInteger.TWO))
    // return scalar -> evaluate(Sqrt.FUNCTION.apply(scalar), RealScalar.of(rational.numerator()));
    // }
    Objects.requireNonNull(exponent);
    return scalar -> evaluate(scalar, exponent);
  }

  /** @param exponent
   * @return function that maps a scalar to scalar ^ exponent */
  public static ScalarUnaryOperator function(Number exponent) {
    return function(RealScalar.of(exponent));
  }

  // ---
  /** @param scalar
   * @param exponent
   * @return scalar ^ exponent */
  private static Scalar evaluate(Scalar scalar, Scalar exponent) {
    // TODO TENSOR for Power.function it would me more efficient to do preprocessing exponent w/o scalar
    if (scalar instanceof PowerInterface powerInterface)
      return powerInterface.power(exponent);
    Optional<BigInteger> optional = Scalars.optionalBigInteger(exponent);
    if (optional.isPresent())
      return Scalars.mul().raise(scalar, optional.orElseThrow());
    throw new Throw(scalar, exponent);
  }
}
