// code by jph
package ch.alpine.tensor.itp;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.mat.VandermondeMatrix;
import ch.alpine.tensor.mat.pi.LeastSquares;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.num.Polynomial;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Fit.html">Fit</a>
 * 
 * @see InterpolatingPolynomial */
public enum Fit {
  ;
  /** @param xdata vector
   * @param ydata vector
   * @param degree of polynomial non-negative strictly less than xdata.length()
   * @return polynomial function */
  public static ScalarUnaryOperator polynomial(Tensor xdata, Tensor ydata, int degree) {
    return Polynomial.of(polynomial_coeffs(xdata, ydata, degree));
  }

  /** @param xdata
   * @param ydata
   * @param degree of polynomial non-negative strictly less than xdata.length()
   * @return coefficients to polynomial as vector of length degree + 1
   * @see Polynomial
   * @see LeastSquares */
  public static Tensor polynomial_coeffs(Tensor xdata, Tensor ydata, int degree) {
    Integers.requirePositive(xdata.length() - degree);
    return xdata.length() == degree + 1 //
        ? LinearSolve.of(VandermondeMatrix.of(xdata), ydata)
        : LeastSquares.of(VandermondeMatrix.of(xdata, degree), ydata);
  }
}
