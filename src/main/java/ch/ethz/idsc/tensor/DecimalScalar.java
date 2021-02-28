// code by jph
package ch.ethz.idsc.tensor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.OptionalInt;

import ch.ethz.idsc.tensor.api.ChopInterface;
import ch.ethz.idsc.tensor.api.NInterface;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.N;

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
 * @see N
 * @see Pi */
public class DecimalScalar extends AbstractRealScalar implements //
    ChopInterface, NInterface, Serializable {
  private static final int DEFAULT_CONTEXT = 34;
  private static final Scalar DECIMAL_ZERO = of(BigDecimal.ZERO);
  private static final Scalar DECIMAL_ONE = of(BigDecimal.ONE);
  /** BigDecimal precision of a double */
  private static final int DOUBLE_PRECISION = 17;

  /** @param bigDecimal
   * @return */
  public static Scalar of(BigDecimal bigDecimal) {
    int precision = bigDecimal.precision();
    return new DecimalScalar(bigDecimal, precision <= DEFAULT_CONTEXT ? DEFAULT_CONTEXT : precision);
  }

  /** @param string
   * @return */
  public static Scalar of(String string) {
    return of(new BigDecimal(string));
  }

  /** @param string
   * @param precision
   * @return scalar with value encoded as {@link BigDecimal(string)} */
  public static Scalar of(String string, int precision) {
    return new DecimalScalar(new BigDecimal(string), precision);
  }

  /***************************************************/
  private final BigDecimal value;
  private final int precision;

  /* package */ DecimalScalar(BigDecimal value, int precision) {
    this.value = value;
    this.precision = precision;
  }

  @Override // from Scalar
  public Scalar negate() {
    return of(value.negate());
  }

  @Override // from Scalar
  public Scalar reciprocal() {
    return of(BigDecimal.ONE.divide(value, mathContext()));
  }

  @Override // from Scalar
  public Scalar multiply(Scalar scalar) {
    if (scalar instanceof DecimalScalar) {
      DecimalScalar decimalScalar = (DecimalScalar) scalar;
      return of(value.multiply(decimalScalar.number()));
    }
    if (scalar instanceof RationalScalar) {
      RationalScalar rationalScalar = (RationalScalar) scalar;
      return of(value.multiply(rationalScalar.toBigDecimal(mathContext())));
    }
    return scalar.multiply(this);
  }

  @Override // from Scalar
  public Scalar divide(Scalar scalar) {
    if (scalar instanceof DecimalScalar) {
      DecimalScalar decimalScalar = (DecimalScalar) scalar;
      return of(value.divide(decimalScalar.number(), hint(precision, decimalScalar.precision())));
    }
    if (scalar instanceof RationalScalar) {
      RationalScalar reciprocal = (RationalScalar) scalar.reciprocal();
      return of(value.multiply(reciprocal.toBigDecimal(mathContext()), mathContext()));
    }
    return scalar.under(this);
  }

  @Override // from Scalar
  public Scalar under(Scalar scalar) {
    if (scalar instanceof DecimalScalar) {
      DecimalScalar decimalScalar = (DecimalScalar) scalar;
      return of(decimalScalar.number().divide(value, hint(precision, decimalScalar.precision())));
    }
    if (scalar instanceof RationalScalar) {
      RationalScalar rationalScalar = (RationalScalar) scalar;
      return of(rationalScalar.toBigDecimal(mathContext()).divide(value, mathContext()));
    }
    return scalar.divide(this);
  }

  @Override // from DecimalScalar
  public BigDecimal number() {
    return value;
  }

  public int precision() {
    return precision;
  }

  @Override // from Scalar
  public Scalar zero() {
    return DECIMAL_ZERO;
  }

  @Override // from Scalar
  public Scalar one() {
    return DECIMAL_ONE;
  }

  private static MathContext hint(int precision1, int precision2) {
    return new MathContext(Math.max(precision1, precision2), RoundingMode.HALF_EVEN);
  }

  /***************************************************/
  @Override // from AbstractScalar
  protected Scalar plus(Scalar scalar) {
    if (scalar instanceof DecimalScalar) {
      DecimalScalar decimalScalar = (DecimalScalar) scalar;
      return of(value.add(decimalScalar.number()));
    }
    if (scalar instanceof RationalScalar) {
      RationalScalar rationalScalar = (RationalScalar) scalar;
      return of(value.add(rationalScalar.toBigDecimal(mathContext())));
    }
    return scalar.add(this);
  }

  /***************************************************/
  @Override // from AbstractRealScalar
  public Scalar arg() {
    return isNonNegative() ? ZERO : Pi.in(precision);
  }

  @Override // from ChopInterface
  public Scalar chop(Chop chop) {
    return value.abs().doubleValue() < chop.threshold() ? ZERO : this;
  }

  @Override // from RoundingInterface
  public Scalar ceiling() {
    return RationalScalar.of(StaticHelper.ceiling(value), BigInteger.ONE);
  }

  @Override // from Comparable<Scalar>
  public int compareTo(Scalar scalar) {
    if (scalar instanceof DecimalScalar) {
      DecimalScalar decimalScalar = (DecimalScalar) scalar;
      return value.compareTo(decimalScalar.number());
    }
    @SuppressWarnings("unchecked")
    Comparable<Scalar> comparable = (Comparable<Scalar>) N.in(precision).apply(scalar);
    return -comparable.compareTo(this);
  }

  @Override // from RoundingInterface
  public Scalar floor() {
    return RationalScalar.of(StaticHelper.floor(value), BigInteger.ONE);
  }

  @Override // from ExpInterface
  public Scalar exp() {
    return of(BigDecimalMath.exp(value, mathContext()));
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
    return optionalInt.isPresent() //
        ? of(value.pow(optionalInt.getAsInt(), mathContext()))
        : super.power(exponent);
  }

  @Override // from RoundingInterface
  public Scalar round() {
    return RationalScalar.of(value.setScale(0, RoundingMode.HALF_UP).toBigIntegerExact(), BigInteger.ONE);
  }

  @Override // from AbstractRealScalar
  protected int signum() {
    return value.signum();
  }

  @Override // from SqrtInterface
  public Scalar sqrt() {
    if (isNonNegative())
      return of(BigDecimalMath.sqrt(value, mathContext()));
    return ComplexScalarImpl.of(zero(), DecimalScalar.of(BigDecimalMath.sqrt(value.negate(), mathContext())));
  }

  @Override // from TrigonometryInterface
  public Scalar cos() {
    return of(BigDecimalMath.cos(value, mathContext()));
  }

  @Override // from TrigonometryInterface
  public Scalar cosh() {
    return of(BigDecimalMath.cosh(value, mathContext()));
  }

  @Override // from TrigonometryInterface
  public Scalar sin() {
    return of(BigDecimalMath.sin(value, mathContext()));
  }

  @Override // from TrigonometryInterface
  public Scalar sinh() {
    return of(BigDecimalMath.sinh(value, mathContext()));
  }

  // helper function used in the implementation of TrigonometryInterface etc.
  private MathContext mathContext() {
    return new MathContext(precision, RoundingMode.HALF_EVEN);
  }

  /***************************************************/
  @Override // from AbstractScalar
  public int hashCode() {
    return value.hashCode();
  }

  @Override // from AbstractScalar
  public boolean equals(Object object) {
    if (object instanceof DecimalScalar) {
      DecimalScalar decimalScalar = (DecimalScalar) object;
      // "equal() only if given BigDecimal's are equal in value and scale,
      // thus 2.0 is not equal to 2.00 when compared by equals()."
      return value.compareTo(decimalScalar.number()) == 0;
    }
    if (object instanceof RationalScalar) {
      RationalScalar rationalScalar = (RationalScalar) object;
      BigDecimal bigDecimal = rationalScalar.toBigDecimal(mathContext());
      return value.compareTo(bigDecimal) == 0;
    }
    if (object instanceof RealScalar) {
      RealScalar realScalar = (RealScalar) object;
      return number().doubleValue() == realScalar.number().doubleValue();
    }
    return Objects.nonNull(object) //
        && object.equals(this);
  }

  @Override // from AbstractScalar
  public String toString() {
    int precision = value.precision();
    // return value.toString() + "`" + precision; // <- changes the appearance of Round._3 etc.
    // solution not elegant because result will be parsed as DoubleScalar
    return value.toString() + (precision <= DOUBLE_PRECISION ? "" : "`" + precision);
  }
}
