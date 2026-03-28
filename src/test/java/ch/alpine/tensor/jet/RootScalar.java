// code by jph
package ch.alpine.tensor.jet;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import ch.alpine.tensor.Complex;
import ch.alpine.tensor.MultiplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.AbsInterface;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.pow.Sqrt;

/** Q[Sqrt[n]] */
public class RootScalar extends MultiplexScalar implements //
    AbsInterface, Serializable {
  /** creator with package visibility
   * 
   * @param re neither a {@link Complex}, or {@link Quantity}
   * @param im neither a {@link Complex}, or {@link Quantity}
   * @return */
  public static Scalar of(Scalar re, Scalar im, Scalar ba) {
    if (re instanceof MultiplexScalar || //
        im instanceof MultiplexScalar || //
        ba instanceof MultiplexScalar)
      throw new Throw(re, im, ba);
    return Scalars.isZero(im) || Scalars.isZero(ba) //
        ? re
        : new RootScalar(re, im, ba);
  }

  // ---
  private final Scalar re;
  private final Scalar im;
  private final Scalar ba;

  RootScalar(Scalar re, Scalar im, Scalar ba) {
    this.re = re;
    this.im = im;
    this.ba = ba;
  }

  @Override // from Scalar
  public Scalar negate() {
    return new RootScalar(re.negate(), im.negate(), ba);
  }

  @Override // from Scalar
  public Scalar multiply(Scalar scalar) {
    if (scalar instanceof RealScalar)
      // TODO TENSOR ALG check for exact precision
      return new RootScalar(re.multiply(scalar), im.multiply(scalar), ba);
    if (scalar instanceof RootScalar rootScalar) {
      if (ba.equals(rootScalar.ba))
        return of( //
            re.multiply(rootScalar.re).add(im.multiply(rootScalar.im).multiply(ba)), //
            re.multiply(rootScalar.im).add(im.multiply(rootScalar.re)), //
            ba);
    }
    throw new UnsupportedOperationException();
  }

  @Override // from Scalar
  public Scalar reciprocal() {
    Scalar den = re.multiply(re).subtract(im.multiply(im).multiply(ba));
    return new RootScalar(re.divide(den), im.negate().divide(den), ba);
  }

  @Override // from Scalar
  public Scalar zero() {
    return re.zero().add(im.zero());
  }

  @Override // from Scalar
  public Scalar one() {
    return re.one();
  }

  // ---
  @Override // from AbstractScalar
  protected Scalar plus(Scalar scalar) {
    if (scalar instanceof RealScalar)
      return new RootScalar(re.add(scalar), im, ba);
    if (scalar instanceof RootScalar rootScalar) {
      if (ba.equals(rootScalar.ba))
        return of( //
            re.add(rootScalar.re), //
            im.add(rootScalar.im), //
            ba);
    }
    throw new UnsupportedOperationException();
  }

  // ---
  @Override // from AbsInterface
  public Scalar abs() { // "complex modulus"
    return Abs.FUNCTION.apply(explicit());
  }

  @Override // from AbsInterface
  public Scalar absSquared() {
    return Abs.FUNCTION.apply(multiply(this));
  }

  @Override
  public Scalar eachMap(UnaryOperator<Scalar> unaryOperator) {
    return of( //
        unaryOperator.apply(re), //
        unaryOperator.apply(im), //
        unaryOperator.apply(ba));
  }

  @Override
  public boolean allMatch(Predicate<Scalar> predicate) {
    return predicate.test(re) //
        && predicate.test(im) //
        && predicate.test(ba);
  }

  public Scalar explicit() {
    return re.add(im.multiply(Sqrt.FUNCTION.apply(ba)));
  }

  // ---
  @Override // from AbstractScalar
  public int hashCode() {
    return Objects.hash(re, im, ba);
  }

  @Override // from AbstractScalar
  public boolean equals(Object object) {
    if (object instanceof RootScalar rootScalar) {
      return re.equals(rootScalar.re) //
          && im.equals(rootScalar.im) //
          && ba.equals(rootScalar.ba);
    }
    return false;
  }

  @Override // from AbstractScalar
  public String toString() {
    return String.format("(%s, %s[%s])", re, im, ba);
  }
}
