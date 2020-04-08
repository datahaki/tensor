// code by jph
package ch.ethz.idsc.tensor.opt;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.NestList;
import ch.ethz.idsc.tensor.mat.LeastSquares;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Fit.html">Fit</a> */
public enum Fit {
  ;
  public static enum Polynomial {
    ;
    /** @param degree non-negative
     * @param xdata vector
     * @param ydata
     * @return */
    public static Tensor of(int degree, Tensor xdata, Tensor ydata) {
      return LeastSquares.of( //
          Tensor.of(xdata.stream() //
              .map(Scalar.class::cast) //
              .map(scalar -> NestList.of(scalar::multiply, RealScalar.ONE, degree))), //
          ydata);
    }
  }
}
