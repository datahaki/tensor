// code by jph
package ch.alpine.tensor.jet;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import ch.alpine.tensor.MultiplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.sca.ply.Polynomial;

/** API EXPERIMENTAL */
class Rapoly extends MultiplexScalar {
  private final Polynomial polynomial;

  public Rapoly(Polynomial polynomial) {
    this.polynomial = polynomial;
  }

  public Polynomial polynomial() {
    return polynomial;
  }

  @Override
  public Scalar multiply(Scalar scalar) {
    return scalar instanceof Rapoly rapoly //
        ? new Rapoly(polynomial.times(rapoly.polynomial))
        : new Rapoly(polynomial.times(scalar));
  }

  @Override
  public Scalar negate() {
    return new Rapoly(polynomial.negate());
  }

  @Override
  public Scalar reciprocal() {
    throw new Throw(this); // needs rational polynomials
  }

  @Override
  public Scalar zero() {
    // TODO test where same as multiply by 0?
    return new Rapoly(polynomial.minus(polynomial));
  }

  @Override
  public Scalar one() {
    return RealScalar.ONE; // TODO
  }

  @Override
  public Scalar eachMap(UnaryOperator<Scalar> unaryOperator) {
    return new Rapoly(Polynomial.of(polynomial.coeffs().map(unaryOperator)));
  }

  @Override
  public boolean allMatch(Predicate<Scalar> predicate) {
    return polynomial.coeffs().stream().map(Scalar.class::cast).allMatch(predicate);
  }

  @Override
  protected Scalar plus(Scalar scalar) {
    return scalar instanceof Rapoly rapoly //
        ? new Rapoly(polynomial.plus(rapoly.polynomial))
        : new Rapoly(polynomial.plus(Polynomial.of(Tensors.of(scalar))));
  }

  @Override
  public int hashCode() {
    return polynomial.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof Rapoly rapoly //
        && polynomial.equals(rapoly.polynomial);
  }

  @Override
  public String toString() {
    return polynomial.toString();
  }
}
