// code by jph
package ch.alpine.tensor.sca.ply;

import java.util.function.Predicate;

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
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.fft.FullConvolve;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.VandermondeMatrix;
import ch.alpine.tensor.mat.pi.LeastSquares;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clip;

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
 * @see #fit(Tensor, Tensor, int)
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
    return new Polynomial(truncate(coeffs, Scalars::isZero));
  }

  /** inspired by
   * <a href="https://reference.wolfram.com/language/ref/Fit.html">Fit</a>
   * 
   * @param xdata vector
   * @param ydata vector
   * @param degree of polynomial non-negative strictly less than xdata.length(), for instance
   * degree == 1 will return a linear polynomial
   * @return polynomial function with coefficients to polynomial as vector of length degree + 1
   * @see Polynomial
   * @see LeastSquares
   * @see InterpolatingPolynomial */
  public static Polynomial fit(Tensor xdata, Tensor ydata, int degree) {
    int excess = Integers.requirePositiveOrZero(xdata.length() - degree - 1);
    // TODO TENSOR ALG use Chebyshev
    return new Polynomial(truncate(excess == 0 //
        ? LinearSolve.of(VandermondeMatrix.of(xdata), ydata)
        : LeastSquares.of(VandermondeMatrix.of(xdata, degree), ydata), Tolerance.CHOP::isZero));
  }

  private static int lastNonZero(Tensor coeffs) {
    return lastNot(coeffs, Scalars::isZero);
  }

  private static Tensor truncate(Tensor coeffs, Predicate<Scalar> predicate) {
    int index = lastNot(coeffs, predicate);
    return coeffs.extract(0, Math.min(Math.max(1, index) + 1, coeffs.length()));
  }

  private static int lastNot(Tensor coeffs, Predicate<Scalar> predicate) {
    for (int index = coeffs.length() - 1; 0 <= index; --index)
      if (!predicate.test(coeffs.Get(index)))
        return index;
    return -1;
  }

  // ---
  private final Tensor coeffs;

  private Polynomial(Tensor coeffs) {
    super(Reverse.of(coeffs));
    this.coeffs = coeffs;
  }

  /** Remark: consistent with Mathematica's CoefficientList
   * 
   * @return coefficients without tailing zeros */
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
    Scalar a = coeffs.Get(0).zero(); // zero() is required for DateTime
    Scalar b = coeffs.Get(1);
    return QuantityUnit.of(a).add(QuantityUnit.of(b).negate()); // of domain
  }

  /** @return unit of values */
  public Unit getUnitValues() {
    return QuantityUnit.of(coeffs.Get(0));
  }

  /** Applications
   * <ul>
   * <li>Newton scheme
   * <li>verification of polynomial reproduction by Hermite subdivision schemes
   * <ul>
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
   * @return polynomial that is the derivative of this polynomial */
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

  /** @return polynomial that is an indefinite integral of this polynomial */
  public Polynomial antiderivative() {
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

  /** Example:
   * <pre>
   * {3[m*s^-1], -4[m*s^-2]} . gain(2) == {0[m*s], 0[m], 3[m*s^-1], -4[m*s^-2]}
   * </pre>
   * 
   * @param order non-negative
   * @return this times x^order
   * @throws Exception if given order is negative
   * @see #moment(int, Scalar, Scalar) */
  public Polynomial gain(int order) {
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

  /** @param order non-negative
   * @param x_lo
   * @param x_hi
   * @return Integrate[ x ^ order * this(x), {x_lo, z_hi}] */
  public Scalar moment(int order, Scalar x_lo, Scalar x_hi) {
    Polynomial polynomial = gain(order).antiderivative();
    return polynomial.apply(x_hi).subtract(polynomial.apply(x_lo));
  }

  /** @param order non-negative
   * @param clip
   * @return */
  public Scalar moment(int order, Clip clip) {
    return moment(order, clip.min(), clip.max());
  }

  /** @param polynomial
   * @return sum of this and given polynomial */
  public Polynomial plus(Polynomial polynomial) {
    int n1 = coeffs.length();
    int n2 = polynomial.coeffs.length();
    return n1 <= n2 //
        ? of(Join.of(coeffs.add(polynomial.coeffs.extract(0, n1)), polynomial.coeffs.extract(n1, n2)))
        : of(Join.of(polynomial.coeffs.add(coeffs.extract(0, n2)), coeffs.extract(n2, n1)));
  }

  /** @param scalar
   * @return polynomial with scalar added to constant coefficient */
  public Polynomial plus(Scalar scalar) {
    Tensor coeffs = coeffs();
    coeffs.set(scalar::add, 0);
    return of(coeffs);
  }

  /** @param polynomial
   * @return this minus given polynomial */
  public Polynomial minus(Polynomial polynomial) {
    return plus(polynomial.negate());
  }

  /** @return negative of this polynomial */
  public Polynomial negate() {
    return new Polynomial(coeffs.negate());
  }

  public Polynomial zero() {
    return of(coeffs.map(Scalar::zero));
  }

  public Scalar one() {
    return coeffs.Get(0).one();
  }

  /** @param polynomial
   * @return polynomial that is the product of this and given polynomials */
  public Polynomial times(Polynomial polynomial) {
    return of(FullConvolve.of(coeffs, polynomial.coeffs()));
  }

  /** @param scalar
   * @return polynomial this times given scalar */
  public Polynomial times(Scalar scalar) {
    return of(coeffs.multiply(scalar));
  }

  /** @param scalar
   * @return polynomial this divided by given scalar */
  public Polynomial divide(Scalar scalar) {
    return of(coeffs.divide(scalar));
  }

  /** @param chop
   * @return polynomial with close-to-zero leading coefficients dropped */
  public Polynomial chop(Chop chop) {
    return new Polynomial(truncate(coeffs, chop::isZero));
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
    return object instanceof Polynomial //
        && coeffs.equals(((Polynomial) object).coeffs);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("Polynomial", coeffs);
  }
}
