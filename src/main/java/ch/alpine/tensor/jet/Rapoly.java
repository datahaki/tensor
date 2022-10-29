// code by jph
package ch.alpine.tensor.jet;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import ch.alpine.tensor.MultiplexScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.sca.ply.Polynomial;

/** EXPERIMENTAL */
class Rapoly extends MultiplexScalar {
  /** @param polynomial
   * @return */
  public static Rapoly of(Polynomial polynomial) {
    return new Rapoly(Objects.requireNonNull(polynomial));
  }

  /** @param coeffs of polynomial
   * @return */
  public static Rapoly of(Tensor coeffs) {
    return new Rapoly(Polynomial.of(coeffs));
  }

  // ---
  private final Polynomial polynomial;

  private Rapoly(Polynomial polynomial) {
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
    return new Rapoly(polynomial.zero());
  }

  @Override
  public Scalar one() {
    return polynomial.one();
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
        : new Rapoly(polynomial.plus(scalar));
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
