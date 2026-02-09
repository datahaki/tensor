// code by jph
package ch.alpine.tensor.mat.ev;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import ch.alpine.tensor.MultiplexScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.sca.ply.Polynomial;

/** EXPERIMENTAL TENSOR
 * 
 * wrapper of a polynomial that simplifies to a real/complex scalar when
 * higher coefficients are zero. */
/* package */ class Rapoly extends MultiplexScalar {
  /** @param polynomial
   * @return */
  public static Scalar of(Polynomial polynomial) {
    return 0 < polynomial.degree() //
        ? new Rapoly(polynomial)
        : polynomial.coeffs().Get(0);
  }

  /** @param coeffs of polynomial
   * @return */
  @PackageTestAccess
  static Scalar of(Tensor coeffs) {
    return of(Polynomial.of(coeffs));
  }

  // ---
  private final Polynomial polynomial;

  /** @param polynomial non-constant */
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
        : of(polynomial.times(scalar));
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
    return of(polynomial.zero());
  }

  @Override
  public Scalar one() {
    // multiplicative neutral element, for instance RealScalar.ONE
    return polynomial.one();
  }

  @Override
  public Scalar eachMap(UnaryOperator<Scalar> unaryOperator) {
    return new Rapoly(Polynomial.of(polynomial.coeffs().maps(unaryOperator)));
  }

  @Override
  public boolean allMatch(Predicate<Scalar> predicate) {
    return polynomial.coeffs().stream().map(Scalar.class::cast).allMatch(predicate);
  }

  @Override
  protected Scalar plus(Scalar scalar) {
    return scalar instanceof Rapoly rapoly //
        ? of(polynomial.plus(rapoly.polynomial))
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
