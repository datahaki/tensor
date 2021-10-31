// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.api.ScalarUnaryOperator;

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
   * Given an empty list of coefficients, the operator evaluates to zero for any parameter x
   * <pre>
   * Polynomial.of({}).apply(x) == x.zero()
   * </pre>
   * 
   * @param coeffs of polynomial
   * @return evaluation of polynomial for scalar input
   * @throws Exception if input is not a vector */
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
   * @param coeffs
   * @return coefficients of polynomial that is the derivative of the polynomial defined by given coeffs
   * @throws Exception if given coeffs is not a vector */
  public static Tensor derivative_coeffs(Tensor coeffs) {
    int length = coeffs.length();
    return length == 0 //
        ? Tensors.empty()
        : coeffs.extract(1, length).pmul(Range.of(1, length));
  }
}
