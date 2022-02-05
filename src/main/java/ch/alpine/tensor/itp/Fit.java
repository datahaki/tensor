// code by jph
package ch.alpine.tensor.itp;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.mat.Tolerance;
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
   * @return polynomial function with coefficients to polynomial as vector of length degree + 1
   * @see Polynomial
   * @see LeastSquares */
  public static Polynomial polynomial(Tensor xdata, Tensor ydata, int degree) {
    Integers.requirePositive(xdata.length() - degree);
    return Polynomial.of(xdata.length() == degree + 1 //
        ? LinearSolve.of(VandermondeMatrix.of(xdata), ydata)
        : LeastSquares.of(VandermondeMatrix.of(xdata, degree), ydata)).chop(Tolerance.CHOP);
  }
}
