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

/** a RationalScalar corresponds to an element from the field of rational numbers.
 * 
 * a RationalScalar represents an integer fraction, for instance 17/42, or -6/1.
 * 
 * zero().reciprocal() throws a {@link ArithmeticException}.
 * 
 * @implSpec
 * This class is immutable and thread-safe. */
public final class RationalScalar extends AbstractRealScalar implements //
    NInterface, Serializable {
  /** rational number {@code 1/2} with decimal value {@code 0.5} */
  public static final Scalar HALF = of(1, 2);

  /** @param num numerator
   * @param den denominator
   * @return scalar encoding the exact fraction num / den */
  public static Scalar of(BigInteger num, BigInteger den) {
    return new RationalScalar(BigFraction.of(num, den));
  }

  /** @param num numerator
   * @param den denominator
   * @return scalar encoding the exact fraction num / den */
  public static Scalar of(long num, long den) {
    return new RationalScalar(BigFraction.of(num, den));
  }

  /** @param num numerator
   * @return scalar encoding the exact fraction num / 1 */
  /* package */ static Scalar integer(long num) {
    return new RationalScalar(BigFraction.integer(num));
  }

  /** @param num numerator
   * @return scalar encoding the exact fraction num / 1 */
  /* package */ static Scalar integer(BigInteger num) {
    return new RationalScalar(BigFraction.integer(num));
  }

  // ---
  private final BigFraction bigFraction;

  /** private constructor is only called from of(...)
   * 
   * @param bigFraction */
  private RationalScalar(BigFraction bigFraction) {
    this.bigFraction = bigFraction;
  }

  @Override // from Scalar
  public RationalScalar negate() {
    return new RationalScalar(bigFraction.negate());
  }

  @Override // from Scalar
  public Scalar multiply(Scalar scalar) {
    return scalar instanceof RationalScalar //
        ? new RationalScalar(bigFraction.multiply(((RationalScalar) scalar).bigFraction))
        : scalar.multiply(this);
  }

  @Override // from AbstractScalar
  public Scalar divide(Scalar scalar) {
    return scalar instanceof RationalScalar //
        // default implementation in AbstractScalar uses 2x gcd
        ? new RationalScalar(bigFraction.divide(((RationalScalar) scalar).bigFraction))
        : scalar.under(this);
  }

  @Override // from AbstractScalar
  public Scalar under(Scalar scalar) {
    return scalar instanceof RationalScalar
        // default implementation in AbstractScalar uses 2x gcd
        ? new RationalScalar(((RationalScalar) scalar).bigFraction.divide(bigFraction))
        : scalar.divide(this);
  }

  @Override // from Scalar
  public RationalScalar reciprocal() {
    return new RationalScalar(bigFraction.reciprocal());
  }

  @Override // from Scalar
  public Scalar zero() {
    return ZERO;
  }

  @Override // from Scalar
  public Scalar one() {
    return ONE;
  }

  @Override // from Scalar
  public Number number() {
    if (bigFraction.isInteger()) {
      BigInteger bigInteger = numerator();
      try {
        return bigInteger.intValueExact();
      } catch (Exception exception) {
        // ---
      }
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
    return scalar instanceof RationalScalar //
        ? new RationalScalar(bigFraction.add(((RationalScalar) scalar).bigFraction))
        : scalar.add(this);
  }

  // ---
  @Override // from RoundingInterface
  public Scalar ceiling() {
    return round(RoundingMode.CEILING);
  }

  @Override // from Comparable<Scalar>
  public int compareTo(Scalar scalar) {
    if (scalar instanceof RationalScalar)
      return bigFraction.compareTo(((RationalScalar) scalar).bigFraction);
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
      int expInt = optionalInt.getAsInt();
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
   * @return {@link RationalScalar} precision if numerator and denominator are both squares */
  @Override // from AbstractRealScalar
  public Scalar sqrt() {
    boolean isNonNegative = isNonNegative();
    Optional<BigInteger> sqrtnum = BigIntegerMath.sqrt(isNonNegative ? numerator() : numerator().negate());
    if (sqrtnum.isPresent()) {
      Optional<BigInteger> sqrtden = BigIntegerMath.sqrt(denominator());
      if (sqrtden.isPresent()) {
        Scalar sqrt = of(sqrtnum.orElseThrow(), sqrtden.orElseThrow());
        return isNonNegative ? sqrt : ComplexScalarImpl.of(ZERO, sqrt);
      }
    }
    return super.sqrt();
  }

  // ---
  /** @return numerator as {@link BigInteger} */
  public BigInteger numerator() {
    return bigFraction.numerator();
  }

  /** @return denominator as {@link BigInteger},
   * the denominator of a {@link RationalScalar} is always positive */
  public BigInteger denominator() {
    return bigFraction.denominator();
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

  /** @return
   * @see IntegerQ */
  /* package */ boolean isInteger() {
    return bigFraction.isInteger();
  }

  // ---
  @Override // from AbstractScalar
  public int hashCode() {
    return bigFraction.hashCode();
  }

  @Override // from AbstractScalar
  public boolean equals(Object object) {
    return object instanceof RationalScalar //
        ? bigFraction._equals(((RationalScalar) object).bigFraction)
        : Objects.nonNull(object) && object.equals(this);
  }

  @Override // from AbstractScalar
  public String toString() {
    return bigFraction.toString();
  }
}
