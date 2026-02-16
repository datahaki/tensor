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
  /** @param bigInteger
   * @return scalar that represents the integer value */
  public static Scalar integer(BigInteger bigInteger) {
    return new RationalImpl(bigInteger, BigInteger.ONE);
  }

  /** @param value
   * @return big fraction that represents the integer value */
  public static Scalar integer(long value) {
    return new RationalImpl(BigInteger.valueOf(value), BigInteger.ONE);
  }

  /** @param num numerator
   * @param den denominator non-zero
   * @return
   * @throws {@link ArithmeticException} if den is zero */
  static Rational simplify(BigInteger num, BigInteger den) {
    BigInteger gcd = num.gcd(den);
    BigInteger res = den.divide(gcd);
    return res.signum() == 1 //
        ? new RationalImpl(num.divide(gcd), res) //
        : new RationalImpl(num.divide(gcd).negate(), res.negate());
  }

  /** private constructor is only called from of(...)
   * 
   * @param bigFraction */
  /** numerator */
  private final BigInteger num;
  /** denominator (always greater than zero) */
  private final BigInteger den;

  private RationalImpl(BigInteger num, BigInteger den) {
    this.num = num;
    this.den = den;
  }

  @Override // from Scalar
  public Rational negate() {
    return new RationalImpl(num.negate(), den);
  }

  @Override // from Scalar
  public Scalar multiply(Scalar scalar) {
    return scalar instanceof RationalImpl rational //
        ? simplify( //
            num.multiply(rational.num), //
            den.multiply(rational.den)) // denominators are non-zero
        : scalar.multiply(this);
  }

  @Override // from AbstractScalar
  public Scalar divide(Scalar scalar) {
    if (scalar instanceof RationalImpl rational) { //
      // default implementation in AbstractScalar uses 2x gcd
      if (rational.signum() == 0)
        throw new ArithmeticException(rational.den + Rational.DIVIDE + rational.num);
      return simplify( //
          num.multiply(rational.den), //
          den.multiply(rational.num));
    }
    return scalar.under(this);
  }

  @Override // from AbstractScalar
  public Scalar under(Scalar scalar) {
    if (scalar instanceof RationalImpl rational) {
      // default implementation in AbstractScalar uses 2x gcd
      if (signum() == 0)
        throw new ArithmeticException(num + Rational.DIVIDE + den);
      return simplify( //
          den.multiply(rational.num), //
          num.multiply(rational.den));
    }
    return scalar.divide(this);
  }

  @Override // from Scalar
  public Rational reciprocal() {
    int signum = signum();
    if (signum == 0)
      throw new ArithmeticException(den + Rational.DIVIDE + num);
    return signum == 1 //
        ? new RationalImpl(den, num) //
        : new RationalImpl(den.negate(), num.negate()); //
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
        ? simplify( //
            num.multiply(rational.den).add(rational.num.multiply(den)), //
            den.multiply(rational.den))
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
      return num.multiply(rational.den).compareTo(rational.num.multiply(den));
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
          ? simplify(num.pow(expInt), den.pow(expInt))
          : Rational.of(den.pow(-expInt), num.pow(-expInt)); // num could be zero
    }
    return super.power(exponent);
  }

  @Override // from RoundingInterface
  public Scalar round() {
    return round(RoundingMode.HALF_UP);
  }

  @Override // from AbstractRealScalar
  protected int signum() {
    return num.signum();
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
    return num;
  }

  @Override
  public BigInteger denominator() {
    return den;
  }

  /** @param roundingMode
   * @return for instance HALF_UP[5/3] == 2, or FLOOR[5/3] == 1 */
  private Scalar round(RoundingMode roundingMode) {
    return integer(new BigDecimal(num).divide(new BigDecimal(den), 0, roundingMode) //
        .toBigIntegerExact());
  }

  @Override
  public BigDecimal toBigDecimal(MathContext mathContext) {
    return new BigDecimal(numerator()).divide(new BigDecimal(denominator()), mathContext);
  }

  // ---
  @Override // from Object
  public int hashCode() {
    return num.hashCode() + 31 * den.hashCode();
  }

  @Override // from AbstractScalar
  public boolean equals(Object object) {
    return object instanceof RationalImpl rational //
        ? num.equals(rational.num) && den.equals(rational.den) // sufficient since in normal form
        : Objects.nonNull(object) && object.equals(this);
  }

  @Override // from Object
  public String toString() {
    return isInteger() //
        ? num.toString()
        : num.toString() + Rational.DIVIDE + den.toString();
  }
}
