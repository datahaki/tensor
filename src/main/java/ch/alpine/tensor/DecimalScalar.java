// code by jph
package ch.alpine.tensor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.OptionalInt;

import ch.alpine.tensor.api.ChopInterface;
import ch.alpine.tensor.api.InexactScalarMarker;
import ch.alpine.tensor.api.NInterface;
import ch.alpine.tensor.ext.BigDecimalMath;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.N;

/** a decimal scalar encodes a number as {@link BigDecimal}.
 * Unless the precision is explicitly specified, MathContext.DECIMAL128 is used.
 * In particular, {@link DecimalScalar} offers increased precision over {@link DoubleScalar}.
 * 
 * <p>The string representation of a {@link DecimalScalar} is of the form
 * {@code [decimal]`[precision]}. Examples are
 * <pre>
 * 220255.6579480671651695790064528423`34
 * 1.414213562373095048801688724209698`34
 * -0.37840124765396412568631954725591454706`19.69897000433602
 * </pre>
 * [precision] denotes how many digits from left to right are correct.
 * The pattern is consistent with Mathematica.
 * 
 * <p>A division by zero throws an ArithmeticException.
 * 
 * @implSpec
 * This class is immutable and thread-safe.
 * 
 * @see N
 * @see Pi */
public class DecimalScalar extends AbstractRealScalar implements //
    ChopInterface, InexactScalarMarker, NInterface, Serializable {
  private static final int DEFAULT_CONTEXT = 34;
  /** BigDecimal precision of a double */
  private static final int DOUBLE_PRECISION = 17;

  /** @param bigDecimal
   * @param precision
   * @return */
  public static Scalar of(BigDecimal bigDecimal, int precision) {
    return new DecimalScalar(Objects.requireNonNull(bigDecimal), precision);
  }

  /** @param bigDecimal
   * @return */
  public static Scalar of(BigDecimal bigDecimal) {
    int precision = bigDecimal.precision();
    return new DecimalScalar(bigDecimal, precision <= DEFAULT_CONTEXT ? DEFAULT_CONTEXT : precision);
  }

  // ---
  private final BigDecimal value;
  private final int precision;

  /* package */ DecimalScalar(BigDecimal value, int precision) {
    this.value = value;
    this.precision = precision;
  }

  @Override // from Scalar
  public DecimalScalar negate() {
    return new DecimalScalar(value.negate(), precision);
  }

  @Override // from Scalar
  public DecimalScalar reciprocal() {
    return new DecimalScalar(BigDecimal.ONE.divide(value, mathContext()), precision);
  }

  @Override // from Scalar
  public Scalar multiply(Scalar scalar) {
    if (scalar instanceof DecimalScalar decimalScalar) {
      MathContext mathContext = hint(decimalScalar);
      BigDecimal bigDecimal = value.multiply(decimalScalar.number(), mathContext);
      return new DecimalScalar(bigDecimal, mathContext.getPrecision());
    }
    if (scalar instanceof RationalScalar)
      return times((RationalScalar) scalar);
    return scalar.multiply(this);
  }

  @Override // from Scalar
  public Scalar divide(Scalar scalar) {
    if (scalar instanceof DecimalScalar decimalScalar) {
      MathContext mathContext = hint(decimalScalar);
      BigDecimal bigDecimal = value.divide(decimalScalar.number(), mathContext);
      return new DecimalScalar(bigDecimal, mathContext.getPrecision());
    }
    if (scalar instanceof RationalScalar rationalScalar)
      return times(rationalScalar.reciprocal());
    return scalar.under(this);
  }

  @Override // from Scalar
  public Scalar under(Scalar scalar) {
    if (scalar instanceof DecimalScalar decimalScalar) {
      MathContext mathContext = hint(decimalScalar);
      BigDecimal bigDecimal = decimalScalar.number().divide(value, mathContext);
      return new DecimalScalar(bigDecimal, mathContext.getPrecision());
    }
    if (scalar instanceof RationalScalar rationalScalar) {
      MathContext mathContext = mathContext();
      BigDecimal bigDecimal = rationalScalar.toBigDecimal(mathContext).divide(value, mathContext);
      return new DecimalScalar(bigDecimal, mathContext.getPrecision());
    }
    return scalar.divide(this);
  }

  @Override // from Scalar
  public BigDecimal number() {
    return value;
  }

  public int precision() {
    return precision;
  }

  @Override // from Scalar
  public Scalar zero() {
    return StaticHelper.CACHE_0.apply(precision);
  }

  @Override // from Scalar
  public Scalar one() {
    return StaticHelper.CACHE_1.apply(precision);
  }

  // helper function used in the implementation of TrigonometryInterface etc.
  private MathContext mathContext() {
    return new MathContext(precision, RoundingMode.HALF_EVEN);
  }

  private MathContext hint(DecimalScalar decimalScalar) {
    return new MathContext(Math.min(precision, decimalScalar.precision), RoundingMode.HALF_EVEN);
  }

  private Scalar times(RationalScalar rationalScalar) {
    MathContext mathContext = mathContext();
    BigDecimal bigDecimal = value.multiply(rationalScalar.toBigDecimal(mathContext), mathContext);
    return new DecimalScalar(bigDecimal, mathContext.getPrecision());
  }

  // ---
  @Override // from AbstractScalar
  protected Scalar plus(Scalar scalar) {
    if (scalar instanceof DecimalScalar decimalScalar) {
      MathContext mathContext = hint(decimalScalar);
      BigDecimal bigDecimal = value.add(decimalScalar.number(), mathContext);
      return new DecimalScalar(bigDecimal, mathContext.getPrecision());
    }
    if (scalar instanceof RationalScalar rationalScalar) {
      MathContext mathContext = mathContext();
      BigDecimal bigDecimal = value.add(rationalScalar.toBigDecimal(mathContext));
      return new DecimalScalar(bigDecimal, mathContext.getPrecision());
    }
    return scalar.add(this);
  }

  // ---
  @Override // from AbstractRealScalar
  public Scalar arg() {
    return isNonNegative() ? ZERO : Pi.in(precision);
  }

  @Override // from ChopInterface
  public Scalar chop(Chop chop) {
    return Math.abs(value.doubleValue()) < chop.threshold() ? ZERO : this;
  }

  @Override // from RoundingInterface
  public Scalar ceiling() {
    return RationalScalar.integer(BigDecimalMath.ceiling(value));
  }

  @Override // from Comparable<Scalar>
  public int compareTo(Scalar scalar) {
    if (scalar instanceof DecimalScalar decimalScalar)
      return value.compareTo(decimalScalar.number());
    @SuppressWarnings("unchecked")
    Comparable<Scalar> comparable = (Comparable<Scalar>) N.in(precision).apply(scalar);
    return -comparable.compareTo(this);
  }

  @Override // from RoundingInterface
  public Scalar floor() {
    return RationalScalar.integer(BigDecimalMath.floor(value));
  }

  @Override // from ExpInterface
  public Scalar exp() {
    MathContext mathContext = mathContext();
    BigDecimal bigDecimal = BigDecimalMath.exp(value, mathContext);
    return new DecimalScalar(bigDecimal, mathContext.getPrecision());
  }

  @Override // from NInterface
  public Scalar n() {
    // consistent with Mathematica: N[N[Pi, 50]] gives a machine number
    return DoubleScalar.of(value.doubleValue());
  }

  @Override // from NInterface
  public Scalar n(MathContext mathContext) {
    return new DecimalScalar(value.round(mathContext), mathContext.getPrecision());
  }

  @Override // from PowerInterface
  public Scalar power(Scalar exponent) {
    OptionalInt optionalInt = Scalars.optionalInt(exponent);
    if (optionalInt.isPresent()) {
      MathContext mathContext = mathContext();
      BigDecimal bigDecimal = value.pow(optionalInt.getAsInt(), mathContext);
      return new DecimalScalar(bigDecimal, mathContext.getPrecision());
    }
    return super.power(exponent);
  }

  @Override // from RoundingInterface
  public Scalar round() {
    return RationalScalar.integer(BigDecimalMath.round(value));
  }

  @Override // from AbstractRealScalar
  protected int signum() {
    return value.signum();
  }

  @Override // from SqrtInterface
  public Scalar sqrt() {
    if (isNonNegative()) {
      MathContext mathContext = mathContext();
      BigDecimal bigDecimal = BigDecimalMath.sqrt(value, mathContext);
      return new DecimalScalar(bigDecimal, mathContext.getPrecision());
    }
    return ComplexScalarImpl.of(zero(), negate().sqrt());
  }

  @Override // from TrigonometryInterface
  public DecimalScalar cos() {
    MathContext mathContext = mathContext();
    BigDecimal bigDecimal = BigDecimalMath.cos(value, mathContext);
    return new DecimalScalar(bigDecimal, mathContext.getPrecision());
  }

  @Override // from TrigonometryInterface
  public DecimalScalar cosh() {
    MathContext mathContext = mathContext();
    BigDecimal bigDecimal = BigDecimalMath.cosh(value, mathContext);
    return new DecimalScalar(bigDecimal, mathContext.getPrecision());
  }

  @Override // from TrigonometryInterface
  public DecimalScalar sin() {
    MathContext mathContext = mathContext();
    BigDecimal bigDecimal = BigDecimalMath.sin(value, mathContext);
    return new DecimalScalar(bigDecimal, mathContext.getPrecision());
  }

  @Override // from TrigonometryInterface
  public DecimalScalar sinh() {
    MathContext mathContext = mathContext();
    BigDecimal bigDecimal = BigDecimalMath.sinh(value, mathContext);
    return new DecimalScalar(bigDecimal, mathContext.getPrecision());
  }

  @Override
  public boolean isFinite() {
    return true;
  }

  // ---
  @Override // from AbstractScalar
  public int hashCode() {
    return value.hashCode();
  }

  @Override // from AbstractScalar
  public boolean equals(Object object) {
    if (object instanceof DecimalScalar decimalScalar)
      // "equal() only if given BigDecimal's are equal in value and scale,
      // thus 2.0 is not equal to 2.00 when compared by equals()."
      return value.compareTo(decimalScalar.number()) == 0;
    if (object instanceof RationalScalar rationalScalar) {
      BigDecimal bigDecimal = rationalScalar.toBigDecimal(mathContext());
      return value.compareTo(bigDecimal) == 0;
    }
    if (object instanceof RealScalar realScalar)
      return number().doubleValue() == realScalar.number().doubleValue();
    return Objects.nonNull(object) //
        && object.equals(this);
  }

  @Override // from AbstractScalar
  public String toString() {
    int precision = value.precision();
    // return value.toString() + "`" + precision; // <- changes the appearance of Round._3 etc.
    // solution not elegant because result will be parsed as DoubleScalar
    return value + (precision <= DOUBLE_PRECISION ? "" : "`" + precision);
  }
}
