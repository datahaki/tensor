// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.fft.FullConvolve;

/** Evaluation of a polynomial using horner scheme.
 * 
 * <p>Mathematica does not have a dedicated function for polynomial evaluation.
 * In Matlab, there exists
 * <a href="https://www.mathworks.com/help/matlab/ref/polyval.html">polyval</a>.
 * 
 * @implSpec
 * This class is immutable and thread-safe. */
public class Polynomial extends HornerScheme {
  /** polynomial evaluation
   * 
   * <pre>
   * Polynomial.of({a, b, c, d}).apply(x)
   * == a + b*x + c*x^2 + d*x^3
   * == a + (b + (c + d ** x) ** x ) ** x
   * </pre>
   * 
   * @param coeffs of polynomial vector of length at least 1
   * @return evaluation of polynomial for scalar input
   * @throws Exception if input is not a vector
   * @throws Exception if input is empty vector */
  public static Polynomial of(Tensor coeffs) {
    return new Polynomial(VectorQ.require(coeffs).copy());
  }

  // ---
  private final Tensor coeffs;

  private Polynomial(Tensor coeffs) {
    super(Reverse.of(coeffs));
    this.coeffs = coeffs;
  }

  /** @return coefficients */
  public Tensor coeffs() {
    return coeffs.copy();
  }

  /** Applications
   * <ul>
   * <li>Newton scheme
   * <li>verification of polynomial reproduction by Hermite subdivision schemes
   * <ul>
   * 
   * @param coeffs of polynomial
   * @return evaluation of first derivative of polynomial with given coefficients
   * @throws Exception if input is not a vector */
  public Polynomial derivative() {
    return of(StaticHelper.derivative_coeffs(coeffs));
  }

  /** @return polynomial that is an integral function to this polynomial */
  public Polynomial integral() {
    return of(StaticHelper.integral_coeffs(coeffs));
  }

  /** @param polynomial
   * @return polynomial that is the product of this and given polynomials */
  public Polynomial product(Polynomial polynomial) {
    return of(FullConvolve.of(coeffs, polynomial.coeffs()));
  }

  /** @return roots of this polynomial
   * @throws Exception if roots cannot be established
   * @see Roots */
  public Tensor roots() {
    return Roots.of(coeffs);
  }

  @Override // from Object
  public int hashCode() {
    return coeffs.hashCode();
  }

  @Override // from Object
  public boolean equals(Object object) {
    return object instanceof Polynomial polynomial //
        && coeffs.equals(polynomial.coeffs);
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s]", getClass().getSimpleName(), coeffs);
  }
}
