// code by jph
package ch.alpine.tensor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import ch.alpine.tensor.api.ChopInterface;
import ch.alpine.tensor.api.InexactScalarMarker;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.exp.ExpInterface;
import ch.alpine.tensor.sca.pow.SqrtInterface;
import ch.alpine.tensor.sca.tri.TrigonometryInterface;

/** scalar with double precision, 64-bit, MATLAB style
 * 
 * <p>The value of {@link DoubleScalar} is backed by a double type.
 * double types are <em>not</em> closed under multiplicative inversion:
 * <pre>
 * a / b != a * (1.0 / b)
 * </pre>
 * For instance, the smallest double is 4.9E-324.
 * But 1.0 / 4.9E-324 == Infinity.
 * 
 * <p>The range of double values closed under 2x inversion, i.e.
 * value == 1.0 / (1.0 / value) is
 * [5.562684646268010E-309, 1.7976931348623151E308]
 * 
 * <p>zero().reciprocal() equals {@link DoubleScalar#POSITIVE_INFINITY}
 * 
 * <p>DoubleScalar is comparable to Scalar types that implement {@link RealScalar}.
 * 
 * <p>The numeric zero has a sign, i.e. positive +0.0 and negative -0.0 exist.
 * The implementation of DoubleScalar uses the following rules:
 * <ul>
 * <li>DoubleScalar.of(-0.0) is backed by the double value -0.0
 * <li>DoubleScalar.of(-0.0) equals DoubleScalar.of(0.0)
 * <li>their hashCode is also identical, which is not the case when using
 * {@link Double#hashCode(double)}
 * <li>Scalars.compare(DoubleScalar.of(-0.0), DoubleScalar.of(0.0)) gives 0
 * </ul>
 * 
 * <p>Special case:
 * Scalars.fromString("-0.0") gives DoubleScalar.of(0.0)
 * 
 * @implSpec
 * This class is immutable and thread-safe. */
public final class DoubleScalar extends AbstractRealScalar implements //
    ChopInterface, InexactScalarMarker, SqrtInterface, ExpInterface, TrigonometryInterface, Serializable {
  /** real scalar that encodes +Infinity. value is backed by Double.POSITIVE_INFINITY */
  public static final Scalar POSITIVE_INFINITY = of(Double.POSITIVE_INFINITY);
  /** real scalar that encodes -Infinity. value is backed by Double.NEGATIVE_INFINITY */
  public static final Scalar NEGATIVE_INFINITY = of(Double.NEGATIVE_INFINITY);
  /** real scalar that encodes NaN. value is backed by Double.NaN == 0.0d / 0.0
   * field name inspired by Mathematica::Indeterminate */
  public static final Scalar INDETERMINATE = of(Double.NaN);

  /** @param value
   * @return new instance of {@link DoubleScalar} */
  public static Scalar of(double value) {
    return new DoubleScalar(value);
  }

  // ---
  private final double value;

  /** private constructor is only called from of(...)
   * 
   * @param value */
  private DoubleScalar(double value) {
    this.value = value;
  }

  @Override // from Scalar
  public Scalar negate() {
    return of(-value);
  }

  @Override // from Scalar
  public Scalar multiply(Scalar scalar) {
    return scalar instanceof RealScalar //
        ? of(value * scalar.number().doubleValue())
        : scalar.multiply(this);
  }

  // implementation exists because 4.9E-324 / 4.9E-324 != 4.9E-324 * (1 / 4.9E-324)
  @Override // from AbstractScalar
  public Scalar divide(Scalar scalar) {
    // if (scalar instanceof RationalScalar)
    // return of(value * scalar.reciprocal().number().doubleValue());
    return scalar instanceof RealScalar //
        ? of(value / scalar.number().doubleValue())
        : scalar.under(this);
  }

  @Override // from AbstractScalar
  public Scalar under(Scalar scalar) {
    return scalar instanceof RealScalar //
        ? of(scalar.number().doubleValue() / value)
        : scalar.divide(this);
  }

  /** DOUBLE_ZERO.reciprocal() == Double.POSITIVE_INFINITY */
  @Override // from Scalar
  public Scalar reciprocal() {
    return of(1.0 / value);
  }

  @Override // from Scalar
  public Number number() {
    return value;
  }

  // ---
  @Override // from AbstractScalar
  protected Scalar plus(Scalar scalar) {
    return scalar instanceof RealScalar //
        ? of(value + scalar.number().doubleValue())
        : scalar.add(this);
  }
  // ---

  @Override // from AbsInterface
  public Scalar abs() {
    return Double.isNaN(value)//
        ? DoubleScalar.INDETERMINATE
        : super.abs();
  }

  @Override // from ArgInterface
  public Scalar arg() {
    return Double.isNaN(value)//
        ? DoubleScalar.INDETERMINATE
        : super.arg();
  }

  @Override // from Comparable<Scalar>
  public int compareTo(Scalar scalar) {
    if (Double.isNaN(value))
      throw new Throw(this, scalar);
    if (scalar instanceof RealScalar) {
      double other = scalar.number().doubleValue();
      if (Double.isNaN(other))
        throw new Throw(this, scalar);
      if (value == other) // +0.0 == -0.0
        return 0;
      return Double.compare(value, other);
    }
    throw new Throw(this, scalar);
  }

  @Override // from RoundingInterface
  public Scalar ceiling() {
    return isFinite() //
        ? RationalScalar.integer(StaticHelper.ceiling(bigDecimal()))
        : this; // value non finite
  }

  @Override // from ChopInterface
  public Scalar chop(Chop chop) {
    return Math.abs(value) < chop.threshold() ? ZERO : this;
  }

  @Override // from RoundingInterface
  public Scalar floor() {
    return isFinite() //
        ? RationalScalar.integer(StaticHelper.floor(bigDecimal()))
        : this; // value non finite
  }

  @Override // from LogInterface
  public Scalar log() {
    return Double.isNaN(value)//
        ? DoubleScalar.INDETERMINATE
        : super.log();
  }

  /* @return true if the argument is a finite floating-point
   * value; false otherwise (for NaN and infinity arguments). */
  @Override // from InexactScalarMarker
  public boolean isFinite() {
    return Double.isFinite(value);
  }

  @Override // from RoundingInterface
  public Scalar round() {
    return isFinite() //
        ? RationalScalar.integer(bigDecimal().setScale(0, RoundingMode.HALF_UP).toBigIntegerExact())
        : this; // value non finite
  }

  @Override // from AbstractRealScalar
  protected int signum() {
    if (Double.isNaN(value))
      throw new Throw(this);
    return value < 0 ? -1 : (0 == value ? 0 : 1);
  }

  @Override // from SignInterface
  public Scalar sign() {
    return Double.isNaN(value) //
        ? DoubleScalar.INDETERMINATE
        : super.sign();
  }

  @Override // from SqrtInterface
  public Scalar sqrt() {
    return Double.isNaN(value) //
        ? DoubleScalar.INDETERMINATE
        : _sqrt();
  }

  @Override // from ExpInterface
  public Scalar exp() {
    return of(Math.exp(value));
  }

  @Override // from TrigonometryInterface
  public Scalar cos() {
    return of(Math.cos(value));
  }

  @Override // from TrigonometryInterface
  public Scalar cosh() {
    return of(Math.cosh(value));
  }

  @Override // from TrigonometryInterface
  public Scalar sin() {
    return of(Math.sin(value));
  }

  @Override // from TrigonometryInterface
  public Scalar sinh() {
    return of(Math.sinh(value));
  }

  // helper function used for implementation in RoundingInterface
  private BigDecimal bigDecimal() {
    return BigDecimal.valueOf(value);
  }

  // ---
  @Override // from AbstractScalar
  public int hashCode() {
    // ensure that +0.0 and -0.0 return same hash value
    return Double.hashCode(value == 0.0 ? 0.0 : value);
  }

  @Override // from AbstractScalar
  public boolean equals(Object object) {
    return object instanceof RealScalar realScalar //
        && value == realScalar.number().doubleValue();
  }

  @Override // from AbstractScalar
  public String toString() {
    return Double.toString(value);
  }
}
