// code by jph
package ch.alpine.tensor.qty;

import java.io.Serializable;
import java.math.MathContext;

import ch.alpine.tensor.AbstractScalar;
import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.api.ChopInterface;
import ch.alpine.tensor.api.ExactScalarQInterface;
import ch.alpine.tensor.api.NInterface;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.ArcTan;
import ch.alpine.tensor.sca.Arg;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Conjugate;
import ch.alpine.tensor.sca.Floor;
import ch.alpine.tensor.sca.Imag;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.Power;
import ch.alpine.tensor.sca.Real;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.Sqrt;

/** Mathematica 12 does not resolve
 * Quantity[1, "Meters"] + Quantity[1, "Seconds"]
 * Quantity[1, "Meters"] + Quantity[0, "Seconds"]
 * Quantity[0, "Meters"] + Quantity[0, "Seconds"]
 * 
 * Mathematica 12 resolves
 * Quantity[1, "Meters"] + 0 == Quantity[1, "Meters"]
 * Quantity[0, "Meters"] + 0 == Quantity[0, "Meters"]
 * 
 * The tensor library resolves
 * Quantity[1, "Meters"] + Quantity[0, "Seconds"] == Quantity[1, "Meters"]
 * Quantity[0, "Meters"] + Quantity[0, "Seconds"] == 0
 * 
 * @implSpec
 * This class is immutable and thread-safe. */
/* package */ class QuantityImpl extends AbstractScalar implements Quantity, //
    ChopInterface, ExactScalarQInterface, NInterface, Serializable {
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
    if (scalar instanceof Quantity) {
      Quantity quantity = (Quantity) scalar;
      return of(value.multiply(quantity.value()), unit.add(quantity.unit()));
    }
    return ofUnit(value.multiply(scalar));
  }

  @Override // from Scalar
  public Scalar divide(Scalar scalar) {
    if (scalar instanceof Quantity) {
      Quantity quantity = (Quantity) scalar;
      return of(value.divide(quantity.value()), unit.add(quantity.unit().negate()));
    }
    return ofUnit(value.divide(scalar));
  }

  @Override // from Scalar
  public Scalar under(Scalar scalar) {
    if (scalar instanceof Quantity) {
      Quantity quantity = (Quantity) scalar;
      return of(value.under(quantity.value()), unit.negate().add(quantity.unit()));
    }
    return new QuantityImpl(value.under(scalar), unit.negate());
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

  @Override // from Scalar
  public Number number() {
    /* extracting the value of a Quantity to a primitive goes against the spirit
     * of using units in the first place. For instance, 3[s] and 3[h] are from the
     * same scale, but are not identical, despite their value part being identical.
     * The function #number() is available for instances of RealScalar`s, which can
     * be obtained from a Quantity via QuantityMagnitude.
     * 
     * Hint: use
     * scalar -> QuantityMagnitude.SI().in(unit).apply(scalar).number();
     * where unit is the desired reference for instance "kW*h^-1" */
    throw TensorRuntimeException.of(this);
  }

  // ---
  @Override // from AbstractScalar
  protected Scalar plus(Scalar scalar) {
    if (scalar instanceof Quantity) {
      Quantity quantity = (Quantity) scalar;
      if (unit.equals(quantity.unit()))
        return ofUnit(value.add(quantity.value()));
    }
    if (Scalars.isZero(value))
      return scalar.add(value);
    if (Scalars.isZero(scalar))
      return ofUnit(scalar.add(value));
    throw TensorRuntimeException.of(this, scalar);
  }
  // @Override // from AbstractScalar
  // protected Scalar plus(Scalar scalar) {
  // if (scalar instanceof Quantity) {
  // Quantity quantity = (Quantity) scalar;
  // if (unit.equals(quantity.unit()))
  // return ofUnit(value.add(quantity.value()));
  // throw TensorRuntimeException.of(this, scalar);
  // }
  // // if (Scalars.isZero(value))
  // // return scalar.add(value);
  // if (Scalars.isZero(scalar))
  // return ofUnit(scalar.add(value));
  // throw TensorRuntimeException.of(this, scalar);
  // }

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
    if (x instanceof Quantity) {
      Quantity quantity = (Quantity) x;
      if (unit.equals(quantity.unit()))
        return ArcTan.of(quantity.value(), value);
    }
    throw TensorRuntimeException.of(this, x);
  }

  @Override // from ArgInterface
  public Scalar arg() {
    return ofUnit(Arg.FUNCTION.apply(value));
  }

  @Override // from ChopInterface
  public Scalar chop(Chop chop) {
    return ofUnit(chop.apply(value));
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

  @Override // from ExactScalarQInterface
  public boolean isExactScalar() {
    return ExactScalarQ.of(value);
  }

  @Override // from NInterface
  public Scalar n() {
    return ofUnit(N.DOUBLE.apply(value));
  }

  @Override // from NInterface
  public Scalar n(MathContext mathContext) {
    return ofUnit(N.in(mathContext.getPrecision()).apply(value));
  }

  @Override // from PowerInterface
  public Scalar power(Scalar exponent) {
    // exponent has to be RealScalar, otherwise an Exception is thrown
    // Mathematica allows 2[m]^3[s], but the tensor library does not:
    return of(Power.of(value, exponent), unit.multiply(exponent));
  }

  @Override // from RoundingInterface
  public Scalar ceiling() {
    return ofUnit(Ceiling.FUNCTION.apply(value));
  }

  @Override // from RoundingInterface
  public Scalar floor() {
    return ofUnit(Floor.FUNCTION.apply(value));
  }

  @Override // from RoundingInterface
  public Scalar round() {
    return ofUnit(Round.FUNCTION.apply(value));
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

  @Override // from Comparable<Scalar>
  public int compareTo(Scalar scalar) {
    if (scalar instanceof Quantity) {
      Quantity quantity = (Quantity) scalar;
      if (unit.equals(quantity.unit()))
        return Scalars.compare(value, quantity.value());
    }
    throw TensorRuntimeException.of(this, scalar);
  }

  // ---
  @Override // from AbstractScalar
  public int hashCode() {
    return value.hashCode() + 31 * unit.hashCode();
  }

  @Override // from AbstractScalar
  public boolean equals(Object object) {
    if (object instanceof Quantity) {
      Quantity quantity = (Quantity) object;
      return value.equals(quantity.value()) //
          && unit.equals(quantity.unit()); // 2[kg] == 2[kg]
    }
    return false;
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
