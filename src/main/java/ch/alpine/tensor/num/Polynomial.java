// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.fft.FullConvolve;
import ch.alpine.tensor.itp.Fit;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.red.Times;

/** Evaluation of a polynomial using horner scheme.
 * 
 * Remark:
 * A polynomial is stored without tailing zeros in the coefficient vector.
 * two polynomials are considered equal if their coefficient vector (without
 * tailing zeros) are equal.
 * 
 * <p>Mathematica does not have a dedicated function for polynomial evaluation.
 * In Matlab, there exists
 * <a href="https://www.mathworks.com/help/matlab/ref/polyval.html">polyval</a>.
 * 
 * @see Fit#polynomial(Tensor, Tensor, int)
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
   * @throws Exception if input is the empty vector */
  public static Polynomial of(Tensor coeffs) {
    VectorQ.require(coeffs);
    return new Polynomial(coeffs.extract(0, Math.min(Math.max(1, lastNonZero(coeffs)) + 1, coeffs.length())));
  }

  private static int lastNonZero(Tensor coeffs) {
    for (int index = coeffs.length() - 1; 0 <= index; --index)
      if (Scalars.nonZero(coeffs.Get(index)))
        return index;
    return -1;
  }

  // ---
  private final Tensor coeffs;

  private Polynomial(Tensor coeffs) {
    super(Reverse.of(coeffs));
    this.coeffs = coeffs;
  }

  /** @return coefficients without tailing zeros */
  public Tensor coeffs() {
    return coeffs.copy();
  }

  /** @return degree of this polynomial, for instance 0 if this polynomial is constant,
   * 1 for linear etc. */
  public int degree() {
    return lastNonZero(coeffs);
  }

  /** @return unit of domain
   * @throws Exception if {@link #coeffs} has length 1 */
  public Unit getUnitDomain() {
    Scalar a = coeffs.Get(0);
    Scalar b = coeffs.Get(1);
    return QuantityUnit.of(a).add(QuantityUnit.of(b).negate()); // of domain
  }

  /** @return unit of values */
  public Unit getUnitValue() {
    return QuantityUnit.of(coeffs.Get(0));
  }

  /** Applications
   * <ul>
   * <li>Newton scheme
   * <li>verification of polynomial reproduction by Hermite subdivision schemes
   * <ul>
   * 
   * @param coeffs of polynomial
   * @return evaluation of first derivative of polynomial with given coefficients
   * @throws Exception if input is not a vector
   * 
   * Hint: ordering of coefficients is <em>reversed</em> compared to
   * MATLAB::polyval, MATLAB::polyfit, etc. !
   * 
   * Example:
   * <pre>
   * derivative_coeffs[{a, b, c, d}] == {b, 2*c, 3*d}
   * </pre>
   * 
   * Remark:
   * Consider the polynomial that maps temperatures (in Kelvin) to pressure (in bar)
   * with coefficients {-13[bar], 0.27[K^-1*bar]}
   * The derivative maps temperatures to quantities with unit "K^-1*bar"
   * {0.27[K^-1*bar], 0.0[K^-2*bar]}
   * in order to preserve the units of the domain.
   * 
   * @param coeffs
   * @return coefficients of polynomial that is the derivative of the polynomial defined by given coeffs
   * @throws Exception if given coeffs is not a vector */
  public Polynomial derivative() {
    int length = coeffs.length();
    if (length == 2) {
      /* a linear polynomial is mapped to a linear polynomial (with zero factor).
       * this ensures that the unit of the argument in the polynomial evaluation
       * is checked. */
      Scalar b = coeffs.Get(1);
      Unit unit = getUnitDomain().negate(); // of domain
      return of(Tensors.of(b, zero(b, unit)));
    }
    return of(length == 1 //
        ? Tensors.of(coeffs.Get(0).zero())
        : Times.of(coeffs.extract(1, length), Range.of(1, length)));
  }

  /** @return polynomial that is an integral function to this polynomial */
  public Polynomial integral() {
    int length = coeffs.length();
    Tensor tensor = Tensors.reserve(length + 1);
    Scalar a = coeffs.Get(0);
    Unit unit = coeffs.length() == 1 //
        ? Unit.ONE
        : getUnitDomain();
    tensor.append(zero(a, unit));
    for (int index = 0; index < length; ++index)
      tensor.append(coeffs.Get(index).multiply(RationalScalar.of(1, index + 1)));
    return of(tensor);
  }

  /** @param order non-negative
   * @return this times x^order
   * @throws Exception if given order is negative */
  public Polynomial moment(int order) {
    Unit unit = coeffs.length() == 1 //
        ? Unit.ONE
        : getUnitDomain();
    Tensor _coeffs = Join.of(Array.zeros(order), coeffs);
    for (int index = order; 0 < index; --index)
      _coeffs.set(zero(_coeffs.Get(index), unit), index - 1);
    return of(_coeffs);
  }

  /** @param scalar
   * @param unit
   * @return scalar.zero() * Quantity(scalar.one(), unit) */
  private static Scalar zero(Scalar scalar, Unit unit) {
    return Quantity.of(scalar.one().zero(), QuantityUnit.of(scalar).add(unit));
  }

  public Polynomial identity() {
    Tensor c01 = coeffs.extract(0, 2);
    c01.set(Scalar::zero, 0);
    Scalar b = coeffs.Get(1);
    Unit unit = getUnitValue().add(getUnitDomain().negate());
    c01.set(Quantity.of(b.one(), unit), 1);
    return of(c01);
  }

  /** @param polynomial
   * @return sum of this and given polynomial */
  public Polynomial plus(Polynomial polynomial) {
    return degree() <= polynomial.degree() //
        ? of(Join.of(coeffs.add(polynomial.coeffs.extract(0, coeffs.length())), polynomial.coeffs.extract(coeffs.length(), polynomial.coeffs.length())))
        : of(Join.of(polynomial.coeffs.add(coeffs.extract(0, polynomial.coeffs.length())), coeffs.extract(polynomial.coeffs.length(), coeffs.length())));
  }

  /** @param polynomial
   * @return polynomial that is the product of this and given polynomials */
  public Polynomial times(Polynomial polynomial) {
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
