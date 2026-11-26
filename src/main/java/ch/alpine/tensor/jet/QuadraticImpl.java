// code by gemini
package ch.alpine.tensor.jet;

import java.util.Objects;

import ch.alpine.tensor.AbstractScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.qty.Quantity;

public final class QuadraticImpl extends AbstractScalar {
  public static Scalar of(Scalar a, Scalar b, Scalar c) {
    Objects.requireNonNull(a);
    Objects.requireNonNull(b);
    Objects.requireNonNull(c);
    if (Scalars.isZero(b) || Scalars.isZero(c)) {
      return a;
    }
    return new QuadraticImpl(a, b, c);
  }

  private final Scalar a;
  private final Scalar b;
  private final Scalar c;

  private QuadraticImpl(Scalar a, Scalar b, Scalar c) {
    this.a = a;
    this.b = b;
    this.c = c;
  }

  public Scalar a() {
    return a;
  }

  public Scalar b() {
    return b;
  }

  public Scalar c() {
    return c;
  }

  @Override
  public Scalar negate() {
    return new QuadraticImpl(a.negate(), b.negate(), c);
  }

  @Override
  protected Scalar plus(Scalar scalar) {
    if (scalar instanceof QuadraticImpl q && c.equals(q.c)) {
      return of(a.add(q.a), b.add(q.b), c);
    }
    if (scalar instanceof RealScalar && !(scalar instanceof Quantity)) {
      return of(a.add(scalar), b, c);
    }
    // Delegate when the other operand is a different QuadraticImpl, Complex, or Quantity
    if (!(scalar instanceof QuadraticImpl)) {
      return scalar.add(this);
    }
    throw new Throw(this, scalar);
  }

  @Override
  public Scalar multiply(Scalar scalar) {
    if (scalar instanceof QuadraticImpl q && c.equals(q.c)) {
      // (a + b*sqrt(c))(q.a + q.b*sqrt(c))
      // = (a*q.a + b*q.b*c) + (a*q.b + b*q.a)*sqrt(c)
      Scalar realPart = a.multiply(q.a).add(b.multiply(q.b).multiply(c));
      Scalar radPart = a.multiply(q.b).add(b.multiply(q.a));
      return of(realPart, radPart, c);
    }
    if (scalar instanceof RealScalar && !(scalar instanceof Quantity)) {
      return of(a.multiply(scalar), b.multiply(scalar), c);
    }
    if (!(scalar instanceof QuadraticImpl)) {
      return scalar.multiply(this);
    }
    throw new Throw(this, scalar);
  }

  @Override
  public Scalar reciprocal() {
    // 1 / (a + b*sqrt(c)) = (a - b*sqrt(c)) / (a^2 - b^2*c)
    Scalar norm = a.multiply(a).subtract(b.multiply(b).multiply(c));
    if (Scalars.isZero(norm)) {
      throw new ArithmeticException("Scalar is not invertible: " + this);
    }
    return of(a.divide(norm), b.negate().divide(norm), c);
  }

  @Override
  public Scalar zero() {
    return a.zero();
  }

  @Override
  public Scalar one() {
    return a.one();
  }

  @Override
  public Number number() {
    throw new Throw(this);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof QuadraticImpl other)) {
      return false;
    }
    return a.equals(other.a) && b.equals(other.b) && c.equals(other.c);
  }

  @Override
  public int hashCode() {
    return Objects.hash(a, b, c);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(32);
    boolean hasA = Scalars.nonZero(a);
    if (hasA) {
      sb.append(a);
    }
    if (Scalars.nonZero(b)) {
      String bStr = b.toString();
      if (hasA && !bStr.startsWith("-")) {
        sb.append('+');
      }
      if ("1".equals(bStr)) {
        sb.append("Sqrt[").append(c).append(']');
      } else if ("-1".equals(bStr)) {
        sb.append("-Sqrt[").append(c).append(']');
      } else {
        sb.append(bStr).append("*Sqrt[").append(c).append(']');
      }
    }
    return sb.length() == 0 ? "0" : sb.toString();
  }
}
