// code by jph
package ch.alpine.tensor.qty;

import java.io.Serializable;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import ch.alpine.tensor.MultiplexScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.Arg;
import ch.alpine.tensor.sca.Conjugate;
import ch.alpine.tensor.sca.Imag;
import ch.alpine.tensor.sca.Real;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.sca.tri.ArcTan;

/** The addition of quantities with different units is not resolved by Mathematica.
 * 
 * Mathematica 12 does not resolve
 * Quantity[1, "Meters"] + Quantity[1, "Seconds"]
 * Quantity[1, "Meters"] + Quantity[0, "Seconds"]
 * Quantity[0, "Meters"] + Quantity[0, "Seconds"]
 * 
 * Consequently the tensor library also throws an exception when quantities
 * of unequal unit are added. That is even the case, when the magnitude of both
 * quantities is zero:
 * <pre>
 * Quantity[0, "Meters"] + Quantity[0, "Seconds"]
 * </pre>
 * 
 * The tensor library throws an exception whenever two quantities are added that
 * have different units. This also holds when one of the units is {@link Unit#ONE}.
 * 
 * For instance
 * <pre>
 * Quantity[0, "Meters"] + 0 throws an Exception.
 * </pre>
 * 
 * Careful: Mathematica 12 resolves
 * <pre>
 * Quantity[1, "Meters"] + 0 == Quantity[1, "Meters"]
 * Quantity[0, "Meters"] + 0 == Quantity[0, "Meters"]
 * </pre>
 * 
 * <p>Extracting the value of a Quantity to a primitive goes against the spirit
 * of using units in the first place. For instance, 3[s] and 3[h] are from the
 * same scale, but are not identical, despite their value part being identical.
 * The function #number() is available for instances of RealScalar`s, which can
 * be obtained from a Quantity via QuantityMagnitude.
 * 
 * Hint: use
 * scalar -> QuantityMagnitude.SI().in(unit).apply(scalar).number();
 * where unit is the desired reference for instance "kW*h^-1"
 * 
 * <p>Careful: the multiplex scalar only involves the value-part of the Quantity.
 * For example, {@link Round} only affects the value-part. Also, predicates
 * only consider the value-part. For example:
 * <pre>
 * ExactScalarQ[ 10 [m^2.0] ] == true
 * ExactScalarQ[ 10 [m^2.5] ] == true
 * </pre>
 * 
 * @implSpec
 * This class is immutable and thread-safe. */
/* package */ class QuantityImpl extends MultiplexScalar implements Quantity, //
    Serializable {
  /** @param value is assumed to be not instance of {@link Quantity}
   * @param unit
   * @return */
  public static Scalar of(Scalar value, Unit unit) {
    return UnitQ.isOne(unit) //
        ? value
        : new QuantityImpl(value, unit);
  }

  // ---
  /** @param scalar not instance of Quantity
   * @return Quantity[scalar, this.unit] */
  private Quantity ofUnit(Scalar scalar) {
    return new QuantityImpl(scalar, unit);
  }

  private final Scalar value;
  /** unit is never Unit.ONE */
  private final Unit unit;

  private QuantityImpl(Scalar value, Unit unit) {
    this.value = value;
    this.unit = unit;
  }

  @Override // from Quantity
  public Scalar value() {
    return value;
  }

  @Override // from Quantity
  public Unit unit() {
    return unit;
  }

  // ---
  @Override // from Scalar
  public Scalar negate() {
    return ofUnit(value.negate());
  }

  @Override // from Scalar
  public Scalar multiply(Scalar scalar) {
    return scalar instanceof Quantity quantity //
        ? of(value.multiply(quantity.value()), unit.add(quantity.unit()))
        : ofUnit(value.multiply(scalar));
  }

  @Override // from Scalar
  public Scalar divide(Scalar scalar) {
    return scalar instanceof Quantity quantity //
        ? of(value.divide(quantity.value()), unit.add(quantity.unit().negate()))
        : ofUnit(value.divide(scalar));
  }

  @Override // from Scalar
  public Scalar under(Scalar scalar) {
    return scalar instanceof Quantity quantity //
        ? of(value.under(quantity.value()), unit.negate().add(quantity.unit()))
        : new QuantityImpl(value.under(scalar), unit.negate());
  }

  @Override // from Scalar
  public Scalar reciprocal() {
    return new QuantityImpl(value.reciprocal(), unit.negate());
  }

  @Override // from Scalar
  public Scalar zero() {
    return ofUnit(value.zero());
  }

  @Override // from Scalar
  public Scalar one() {
    return value.one();
  }

  // ---
  @Override // from AbstractScalar
  protected Scalar plus(Scalar scalar) {
    if (scalar instanceof Quantity quantity)
      if (unit.equals(quantity.unit()))
        return ofUnit(value.add(quantity.value()));
    throw TensorRuntimeException.of(this, scalar);
  }

  // ---
  @Override // from AbsInterface
  public Scalar abs() {
    return ofUnit(Abs.FUNCTION.apply(value));
  }

  @Override // from AbsInterface
  public Scalar absSquared() {
    return of(AbsSquared.FUNCTION.apply(value), unit.add(unit));
  }

  @Override // from ArcTanInterface
  public Scalar arcTan(Scalar x) {
    if (x instanceof Quantity quantity && unit.equals(quantity.unit()))
      return ArcTan.of(quantity.value(), value);
    throw TensorRuntimeException.of(this, x);
  }

  @Override // from ArgInterface
  public Scalar arg() {
    return Arg.FUNCTION.apply(value);
  }

  @Override // from ConjugateInterface
  public Scalar conjugate() {
    return ofUnit(Conjugate.FUNCTION.apply(value));
  }

  @Override // from ComplexEmbedding
  public Scalar real() {
    return ofUnit(Real.FUNCTION.apply(value));
  }

  @Override // from ComplexEmbedding
  public Scalar imag() {
    return ofUnit(Imag.FUNCTION.apply(value));
  }

  @Override // from PowerInterface
  public Scalar power(Scalar exponent) {
    // exponent has to be RealScalar, otherwise an Exception is thrown
    // Mathematica allows 2[m]^3[s], but the tensor library does not:
    return of(Power.of(value, exponent), unit.multiply(exponent));
  }

  @Override // from SignInterface
  public Scalar sign() {
    return Sign.FUNCTION.apply(value);
  }

  @Override // from SqrtInterface
  public Scalar sqrt() {
    return new QuantityImpl( //
        Sqrt.FUNCTION.apply(value), //
        unit.multiply(RationalScalar.HALF));
  }

  @Override // from MultiplexScalar
  public Scalar eachMap(UnaryOperator<Scalar> unaryOperator) {
    return ofUnit(unaryOperator.apply(value));
  }

  @Override // from MultiplexScalar
  public boolean allMatch(Predicate<Scalar> predicate) {
    return predicate.test(value);
  }

  @Override // from Comparable<Scalar>
  public int compareTo(Scalar scalar) {
    if (scalar instanceof Quantity quantity && unit.equals(quantity.unit()))
      return Scalars.compare(value, quantity.value());
    throw TensorRuntimeException.of(this, scalar);
  }

  // ---
  @Override // from AbstractScalar
  public int hashCode() {
    return value.hashCode() + 31 * unit.hashCode();
  }

  @Override // from AbstractScalar
  public boolean equals(Object object) {
    return object instanceof Quantity quantity //
        && value.equals(quantity.value()) //
        && unit.equals(quantity.unit()); // 2[kg] == 2[kg]
  }

  @Override // from AbstractScalar
  public String toString() {
    String vs = value.toString();
    String us = unit.toString();
    StringBuilder stringBuilder = new StringBuilder(vs.length() + us.length() + 2);
    stringBuilder.append(vs);
    stringBuilder.append(UNIT_OPENING_BRACKET);
    stringBuilder.append(us);
    stringBuilder.append(UNIT_CLOSING_BRACKET);
    return stringBuilder.toString();
  }
}
