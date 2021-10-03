// code by jph
package ch.alpine.tensor.itp;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.VandermondeMatrix;
import ch.alpine.tensor.mat.pi.LeastSquares;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Fit.html">Fit</a> */
public enum Fit {
  ;
  /** @param degree of polynomial
   * @param xdata
   * @param ydata
   * @return */
  public static Tensor polynomial(int degree, Tensor xdata, Tensor ydata) {
    return LeastSquares.of(VandermondeMatrix.of(xdata, degree), ydata);
  }
}
