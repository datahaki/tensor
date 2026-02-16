// code by jph
package ch.alpine.tensor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import ch.alpine.tensor.chq.IntegerQ;

/** a RationalScalar corresponds to an element from the field of rational numbers.
 * 
 * a RationalScalar represents an integer fraction, for instance 17/42, or -6/1.
 * 
 * zero().reciprocal() throws a {@link ArithmeticException}.
 * 
 * @implSpec
 * This class is immutable and thread-safe. */
public interface Rational extends Scalar {
  /** rational number {@code 1/2} with decimal value {@code 0.5} */
  Scalar HALF = of(1, 2);
  Scalar THIRD = of(1, 3);
  String DIVIDE = "/";

  /** @param num numerator
   * @param den denominator
   * @return scalar encoding the exact fraction num / den */
  static Scalar of(BigInteger num, BigInteger den) {
    if (den.signum() == 0)
      throw new ArithmeticException(num + DIVIDE + den);
    return RationalImpl.simplify(num, den);
  }

  /** @param num numerator
   * @param den denominator
   * @return scalar encoding the exact fraction num / den */
  static Scalar of(long num, long den) {
    return of(BigInteger.valueOf(num), BigInteger.valueOf(den));
  }

  @Override
  Rational negate();

  @Override
  Rational reciprocal();

  // ---
  /** @return numerator as {@link BigInteger} */
  BigInteger numerator();

  /** @return denominator as {@link BigInteger},
   * the denominator of a {@link Rational} is always positive */
  BigInteger denominator();

  /** @return
   * @see IntegerQ */
  default boolean isInteger() {
    return denominator().equals(BigInteger.ONE);
  }

  /** @param mathContext
   * @return */
  BigDecimal toBigDecimal(MathContext mathContext);
}
