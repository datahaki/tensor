// code by jph
package ch.ethz.idsc.tensor.itp;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.LeastSquares;
import ch.ethz.idsc.tensor.mat.VandermondeMatrix;

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
