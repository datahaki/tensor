// code by jph
package ch.alpine.tensor.jet;

import java.io.Serializable;
import java.math.MathContext;
import java.util.Objects;

import ch.alpine.tensor.AbstractRealScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.NInterface;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Floor;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.pow.Sqrt;

/** EXPERIMENTAL */
public class Quex extends AbstractRealScalar implements NInterface, Serializable {
  public static Scalar of(long a, long b, long c) {
    return of(RealScalar.of(a), RealScalar.of(b), RealScalar.of(c));
  }

  public static Scalar of(Scalar a, Scalar b, Scalar c) {
    if (a instanceof RationalScalar && //
        b instanceof RationalScalar && //
        c instanceof RationalScalar)
      return Scalars.isZero(b) || Scalars.isZero(c) //
          ? a
          : new Quex(a, b, c);
    return Sqrt.FUNCTION.apply(c).multiply(b).add(a);
  }

  // ---
  private final Scalar a;
  private final Scalar b;
  private final Scalar c;

  private Quex(Scalar a, Scalar b, Scalar c) {
    this.a = a;
    this.b = b;
    this.c = c;
  }

  protected Scalar expand() {
    return Sqrt.FUNCTION.apply(c).multiply(b).add(a);
  }

  @Override
  public Scalar multiply(Scalar scalar) {
    if (scalar instanceof Quex quex && quex.c.equals(c))
      return of( //
          a.multiply(quex.a).add(Times.of(b, quex.b, c)), //
          a.multiply(quex.b).add(b.multiply(quex.a)), //
          c);
    return scalar instanceof RationalScalar //
        ? of(a.multiply(scalar), b.multiply(scalar), c)
        : scalar.multiply(expand());
  }
  // @Override
  // public Scalar divide(Scalar scalar) {
  // return super.divide(scalar);
  // }
  //
  // @Override
  // public Scalar under(Scalar scalar) {
  // return super.under(scalar);
  // }

  @Override
  public Scalar negate() {
    return new Quex(a.negate(), b.negate(), c);
  }

  @Override
  public Scalar reciprocal() {
    Scalar den = a.multiply(a).subtract(Times.of(b, b, c));
    return new Quex(a.divide(den), b.negate().divide(den), c);
  }

  @Override
  public Number number() {
    return expand().number();
  }

  @Override
  public int compareTo(Scalar scalar) {
    if (scalar instanceof Quex quex)
      return Scalars.compare(expand(), quex.expand());
    return Scalars.compare(expand(), scalar);
  }

  @Override // from AbsInterface
  public Scalar abs() { // "complex modulus"
    return Abs.FUNCTION.apply(expand());
  }

  @Override
  public Scalar ceiling() {
    return Ceiling.FUNCTION.apply(expand());
  }

  @Override
  public Scalar floor() {
    return Floor.FUNCTION.apply(expand());
  }

  @Override
  public Scalar round() {
    return Round.FUNCTION.apply(expand());
  }

  @Override
  protected int signum() {
    return Sign.FUNCTION.apply(expand()).number().intValue();
  }

  @Override
  protected Scalar plus(Scalar scalar) {
    if (scalar instanceof Quex quex && quex.c.equals(c))
      return of( //
          a.add(quex.a), //
          b.add(quex.b), //
          c);
    Scalar probe = a.add(scalar);
    return probe instanceof RationalScalar //
        ? of(probe, b, c)
        : expand().add(scalar);
  }

  @Override
  public Scalar n() {
    return N.DOUBLE.apply(expand());
  }

  @Override
  public Scalar n(MathContext mathContext) {
    // TODO TENSOR
    return n();
  }

  @Override
  public int hashCode() {
    return Objects.hash(a, b, c);
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof Quex quex //
        && quex.a.equals(a) //
        && quex.b.equals(b) //
        && quex.c.equals(c);
  }

  @Override
  public String toString() {
    return "(" + a + "+" + b + "*Sqrt[" + c + "])";
  }
}
