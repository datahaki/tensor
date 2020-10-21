// code by jph
package ch.ethz.idsc.tensor.itp;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.NestList;
import ch.ethz.idsc.tensor.mat.LeastSquares;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Fit.html">Fit</a> */
public enum Fit {
  ;
  /** @param degree
   * @param xdata
   * @param ydata
   * @param one
   * @return */
  public static Tensor polynomial(int degree, Tensor xdata, Tensor ydata) {
    return LeastSquares.of( //
        Tensor.of(xdata.stream() //
            .map(Scalar.class::cast) //
            .map(scalar -> NestList.of(scalar::multiply, RealScalar.ONE, degree))), //
        ydata);
  }
}
