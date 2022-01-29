// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.fft.FullConvolve;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.red.Times;

/** Evaluation of a polynomial using horner scheme.
 * 
 * <p>Mathematica does not have a dedicated function for polynomial evaluation.
 * In Matlab, there exists
 * <a href="https://www.mathworks.com/help/matlab/ref/polyval.html">polyval</a>.
 * 
 * <p>In earlier versions of the tensor library, the functionality was
 * called <i>Series</i>. */
public enum Polynomial {
  ;
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
  public static ScalarUnaryOperator of(Tensor coeffs) {
    return new HornerScheme(Reverse.of(VectorQ.require(coeffs)));
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
  public static ScalarUnaryOperator derivative(Tensor coeffs) {
    return of(derivative_coeffs(coeffs));
  }

  /** Hint:
   * ordering of coefficients is <em>reversed</em> compared to
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
  public static Tensor derivative_coeffs(Tensor coeffs) {
    int length = coeffs.length();
    if (length == 2) {
      /* a linear polynomial is mapped to a linear polynomial (with zero factor).
       * this ensures that the unit of the argument in the polynomial evaluation
       * is checked. */
      Scalar a = coeffs.Get(0);
      Scalar b = coeffs.Get(1);
      Unit unit = QuantityUnit.of(b).add(QuantityUnit.of(a).negate()); // of domain
      return Tensors.of(b, b.zero().multiply(Quantity.of(b.one(), unit)));
    }
    return length == 0 //
        ? Tensors.empty()
        : Times.of(coeffs.extract(1, length), Range.of(1, length));
  }

  public static ScalarUnaryOperator integral(Tensor coeffs) {
    return of(integral_coeffs(coeffs));
  }

  /** @param coeffs
   * @return */
  public static Tensor integral_coeffs(Tensor coeffs) {
    // TODO not generic, function should also work for units
    Tensor tensor = Tensors.reserve(coeffs.length() + 1);
    tensor.append(RealScalar.ZERO);
    for (int index = 0; index < coeffs.length(); ++index)
      tensor.append(coeffs.Get(index).multiply(RationalScalar.of(1, index + 1)));
    return tensor;
  }

  public static Tensor product(Tensor c1, Tensor c2) {
    return FullConvolve.with(c1).apply(c2);
  }
}
