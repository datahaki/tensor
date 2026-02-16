// code by jph
package ch.alpine.tensor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

import ch.alpine.tensor.api.NInterface;
import ch.alpine.tensor.chq.IntegerQ;
import ch.alpine.tensor.sca.exp.ExpInterface;
import ch.alpine.tensor.sca.pow.SqrtInterface;
import ch.alpine.tensor.sca.tri.TrigonometryInterface;

/** a RationalScalar corresponds to an element from the field of rational numbers.
 * 
 * a RationalScalar represents an integer fraction, for instance 17/42, or -6/1.
 * 
 * zero().reciprocal() throws a {@link ArithmeticException}.
 * 
 * @implSpec
 * This class is immutable and thread-safe. */
public final class Rational extends AbstractRealScalar implements //
    NInterface, SqrtInterface, ExpInterface, TrigonometryInterface, Serializable {
  /** rational number {@code 1/2} with decimal value {@code 0.5} */
  public static final Scalar HALF = of(1, 2);
  public static final Scalar THIRD = of(1, 3);

  /** @param num numerator
   * @param den denominator
   * @return scalar encoding the exact fraction num / den */
  public static Scalar of(BigInteger num, BigInteger den) {
    return new Rational(BigFraction.of(num, den));
  }

  /** @param num numerator
   * @param den denominator
   * @return scalar encoding the exact fraction num / den */
  public static Scalar of(long num, long den) {
    return new Rational(BigFraction.of(num, den));
  }

  /** @param num numerator
   * @return scalar encoding the exact fraction num / 1 */
  /* package */ static Scalar integer(long num) {
    return new Rational(BigFraction.integer(num));
  }

  /** @param num numerator
   * @return scalar encoding the exact fraction num / 1 */
  /* package */ static Scalar integer(BigInteger num) {
    return new Rational(BigFraction.integer(num));
  }

  // ---
  private final BigFraction bigFraction;

  /** private constructor is only called from of(...)
   * 
   * @param bigFraction */
  private Rational(BigFraction bigFraction) {
    this.bigFraction = bigFraction;
  }

  @Override // from Scalar
  public Rational negate() {
    return new Rational(bigFraction.negate());
  }

  @Override // from Scalar
  public Scalar multiply(Scalar scalar) {
    return scalar instanceof Rational rational //
        ? new Rational(bigFraction.multiply(rational.bigFraction))
        : scalar.multiply(this);
  }

  @Override // from AbstractScalar
  public Scalar divide(Scalar scalar) {
    return scalar instanceof Rational rational //
        // default implementation in AbstractScalar uses 2x gcd
        ? new Rational(bigFraction.divide(rational.bigFraction))
        : scalar.under(this);
  }

  @Override // from AbstractScalar
  public Scalar under(Scalar scalar) {
    return scalar instanceof Rational rational
        // default implementation in AbstractScalar uses 2x gcd
        ? new Rational(rational.bigFraction.divide(bigFraction))
        : scalar.divide(this);
  }

  @Override // from Scalar
  public Rational reciprocal() {
    return new Rational(bigFraction.reciprocal());
  }

  @Override // from Scalar
  public Number number() {
    if (isInteger()) {
      BigInteger bigInteger = numerator();
      if (bigInteger.bitLength() < 32) // quick hint
        try {
          return bigInteger.intValueExact();
        } catch (Exception exception) {
          // ---
        }
      if (bigInteger.bitLength() < 64) // quick hint
        try {
          return bigInteger.longValueExact();
        } catch (Exception exception) {
          // ---
        }
      return bigInteger;
    }
    return toBigDecimal(MathContext.DECIMAL64).doubleValue();
  }

  // ---
  @Override // from AbstractScalar
  protected Scalar plus(Scalar scalar) {
    return scalar instanceof Rational rational //
        ? new Rational(bigFraction.add(rational.bigFraction))
        : scalar.add(this);
  }

  // ---
  @Override // from RoundingInterface
  public Scalar ceiling() {
    return round(RoundingMode.CEILING);
  }

  @Override // from Comparable<Scalar>
  public int compareTo(Scalar scalar) {
    if (scalar instanceof Rational rational)
      return bigFraction.compareTo(rational.bigFraction);
    @SuppressWarnings("unchecked")
    Comparable<Scalar> comparable = (Comparable<Scalar>) scalar;
    return -comparable.compareTo(this);
  }

  @Override // from RoundingInterface
  public Scalar floor() {
    return round(RoundingMode.FLOOR);
  }

  @Override // from NInterface
  public Scalar n() {
    return DoubleScalar.of(toBigDecimal(MathContext.DECIMAL64).doubleValue());
  }

  @Override // from NInterface
  public Scalar n(MathContext mathContext) {
    return DecimalScalar.of(toBigDecimal(mathContext), mathContext.getPrecision());
  }

  @Override // from AbstractRealScalar
  public Scalar power(Scalar exponent) {
    OptionalInt optionalInt = Scalars.optionalInt(exponent);
    if (optionalInt.isPresent()) {
      int expInt = optionalInt.orElseThrow();
      return 0 <= expInt //
          ? of(numerator().pow(expInt), denominator().pow(expInt))
          : of(denominator().pow(-expInt), numerator().pow(-expInt));
    }
    return super.power(exponent);
  }

  @Override // from RoundingInterface
  public Scalar round() {
    return round(RoundingMode.HALF_UP);
  }

  @Override // from AbstractRealScalar
  protected int signum() {
    return bigFraction.signum();
  }

  /** Example: sqrt(16/25) == 4/5
   * 
   * @return {@link Rational} precision if numerator and denominator are both squares */
  @Override // from AbstractRealScalar
  public Scalar sqrt() {
    boolean isNonNegative = isNonNegative();
    Optional<BigInteger> sqrtnum = StaticHelper.sqrt(isNonNegative ? numerator() : numerator().negate());
    if (sqrtnum.isPresent()) {
      Optional<BigInteger> sqrtden = StaticHelper.sqrt(denominator());
      if (sqrtden.isPresent()) {
        Scalar sqrt = of(sqrtnum.orElseThrow(), sqrtden.orElseThrow());
        return isNonNegative ? sqrt : ComplexScalarImpl.of(ZERO, sqrt);
      }
    }
    return _sqrt();
  }

  @Override // from ExpInterface
  public Scalar exp() {
    return DoubleScalar.of(Math.exp(number().doubleValue()));
  }

  @Override // from TrigonometryInterface
  public Scalar cos() {
    return DoubleScalar.of(Math.cos(number().doubleValue()));
  }

  @Override // from TrigonometryInterface
  public Scalar cosh() {
    return DoubleScalar.of(Math.cosh(number().doubleValue()));
  }

  @Override // from TrigonometryInterface
  public Scalar sin() {
    return DoubleScalar.of(Math.sin(number().doubleValue()));
  }

  @Override // from TrigonometryInterface
  public Scalar sinh() {
    return DoubleScalar.of(Math.sinh(number().doubleValue()));
  }

  // ---
  /** @return numerator as {@link BigInteger} */
  public BigInteger numerator() {
    return bigFraction.numerator();
  }

  /** @return denominator as {@link BigInteger},
   * the denominator of a {@link Rational} is always positive */
  public BigInteger denominator() {
    return bigFraction.denominator();
  }

  /** @return
   * @see IntegerQ */
  public boolean isInteger() {
    return bigFraction.isInteger();
  }

  /** @param roundingMode
   * @return for instance HALF_UP[5/3] == 2, or FLOOR[5/3] == 1 */
  private Scalar round(RoundingMode roundingMode) {
    return integer(new BigDecimal(numerator()) //
        .divide(new BigDecimal(denominator()), 0, roundingMode) //
        .toBigIntegerExact());
  }

  /** @param mathContext
   * @return */
  /* package */ BigDecimal toBigDecimal(MathContext mathContext) {
    return new BigDecimal(numerator()).divide(new BigDecimal(denominator()), mathContext);
  }

  // ---
  @Override // from AbstractScalar
  public int hashCode() {
    return bigFraction.hashCode();
  }

  @Override // from AbstractScalar
  public boolean equals(Object object) {
    return object instanceof Rational rational //
        ? bigFraction._equals(rational.bigFraction)
        : Objects.nonNull(object) && object.equals(this);
  }

  @Override // from AbstractScalar
  public String toString() {
    return bigFraction.toString();
  }
}
