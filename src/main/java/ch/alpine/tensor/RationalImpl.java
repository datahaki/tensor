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
/* package */ class RationalImpl extends AbstractRealScalar implements Rational, //
    NInterface, SqrtInterface, ExpInterface, TrigonometryInterface, Serializable {
  private final BigFraction bigFraction;

  /** private constructor is only called from of(...)
   * 
   * @param bigFraction */
  public RationalImpl(BigFraction bigFraction) {
    this.bigFraction = bigFraction;
  }

  @Override // from Scalar
  public Rational negate() {
    return new RationalImpl(bigFraction.negate());
  }

  @Override // from Scalar
  public Scalar multiply(Scalar scalar) {
    return scalar instanceof RationalImpl rational //
        ? new RationalImpl(bigFraction.multiply(rational.bigFraction))
        : scalar.multiply(this);
  }

  @Override // from AbstractScalar
  public Scalar divide(Scalar scalar) {
    return scalar instanceof RationalImpl rational //
        // default implementation in AbstractScalar uses 2x gcd
        ? new RationalImpl(bigFraction.divide(rational.bigFraction))
        : scalar.under(this);
  }

  @Override // from AbstractScalar
  public Scalar under(Scalar scalar) {
    return scalar instanceof RationalImpl rational
        // default implementation in AbstractScalar uses 2x gcd
        ? new RationalImpl(rational.bigFraction.divide(bigFraction))
        : scalar.divide(this);
  }

  @Override // from Scalar
  public Rational reciprocal() {
    return new RationalImpl(bigFraction.reciprocal());
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
    return scalar instanceof RationalImpl rational //
        ? new RationalImpl(bigFraction.add(rational.bigFraction))
        : scalar.add(this);
  }

  // ---
  @Override // from RoundingInterface
  public Scalar ceiling() {
    return round(RoundingMode.CEILING);
  }

  @Override // from Comparable<Scalar>
  public int compareTo(Scalar scalar) {
    if (scalar instanceof RationalImpl rational)
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
          ? Rational.of(numerator().pow(expInt), denominator().pow(expInt))
          : Rational.of(denominator().pow(-expInt), numerator().pow(-expInt));
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
   * @return {@link RationalImpl} precision if numerator and denominator are both squares */
  @Override // from AbstractRealScalar
  public Scalar sqrt() {
    boolean isNonNegative = isNonNegative();
    Optional<BigInteger> sqrtnum = StaticHelper.sqrt(isNonNegative ? numerator() : numerator().negate());
    if (sqrtnum.isPresent()) {
      Optional<BigInteger> sqrtden = StaticHelper.sqrt(denominator());
      if (sqrtden.isPresent()) {
        Scalar sqrt = Rational.of(sqrtnum.orElseThrow(), sqrtden.orElseThrow());
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

  @Override
  public BigInteger numerator() {
    return bigFraction.numerator();
  }

  @Override
  public BigInteger denominator() {
    return bigFraction.denominator();
  }

  @Override
  public boolean isInteger() {
    return bigFraction.isInteger();
  }

  /** @param roundingMode
   * @return for instance HALF_UP[5/3] == 2, or FLOOR[5/3] == 1 */
  private Scalar round(RoundingMode roundingMode) {
    return new RationalImpl(BigFraction.integer(new BigDecimal(numerator()) //
        .divide(new BigDecimal(denominator()), 0, roundingMode) //
        .toBigIntegerExact()));
  }

  @Override
  public BigDecimal toBigDecimal(MathContext mathContext) {
    return new BigDecimal(numerator()).divide(new BigDecimal(denominator()), mathContext);
  }

  // ---
  @Override // from AbstractScalar
  public int hashCode() {
    return bigFraction.hashCode();
  }

  @Override // from AbstractScalar
  public boolean equals(Object object) {
    return object instanceof RationalImpl rational //
        ? bigFraction._equals(rational.bigFraction)
        : Objects.nonNull(object) && object.equals(this);
  }

  @Override // from AbstractScalar
  public String toString() {
    return bigFraction.toString();
  }
}
