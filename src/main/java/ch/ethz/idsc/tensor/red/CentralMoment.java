// code by jph
package ch.ethz.idsc.tensor.red;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Power;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/CentralMoment.html">CentralMoment</a> */
public enum CentralMoment {
  ;
  /** @param vector with at least one element
   * @param order of moment
   * @return
   * @throws Exception if given vector is empty */
  public static Scalar of(Tensor vector, Scalar order) {
    Scalar nmean = (Scalar) Mean.of(vector).negate();
    return vector.stream() //
        .map(nmean::add) //
        .map(Power.function(order)) //
        .reduce(Scalar::add) //
        .get() //
        .divide(RealScalar.of(vector.length()));
  }

  /** @param vector
   * @param order of moment
   * @return */
  public static Scalar of(Tensor vector, Number order) {
    return of(vector, RealScalar.of(order));
  }
}
