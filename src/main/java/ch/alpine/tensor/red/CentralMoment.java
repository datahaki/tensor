// code by jph
package ch.alpine.tensor.red;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.pdf.CentralMomentInterface;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.sca.pow.Power;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/CentralMoment.html">CentralMoment</a> */
public enum CentralMoment {
  ;
  /** @param vector with at least one element
   * @param order of moment
   * @return
   * @throws Exception if given vector is empty */
  public static Scalar of(Tensor vector, Scalar order) {
    Scalar nmean = Mean.ofVector(vector).negate();
    return vector.stream() //
        .map(nmean::add) //
        .map(Power.function(order)) //
        .reduce(Scalar::add) //
        .orElseThrow() //
        .divide(RealScalar.of(vector.length()));
  }

  /** @param vector
   * @param order of central moment
   * @return central moment of given distribution and order */
  public static Scalar of(Tensor vector, Number order) {
    return of(vector, RealScalar.of(order));
  }

  /** Remark:
   * all symmetric distributions have CentralMoment[uneven order] == 0
   * 
   * @param distribution
   * @param order non-negative
   * @return central moment of given distribution and order
   * @throws Exception if order is negative */
  public static Scalar of(Distribution distribution, int order) {
    if (distribution instanceof CentralMomentInterface)
      return ((CentralMomentInterface) distribution).centralMoment(Integers.requirePositiveOrZero(order));
    switch (order) {
    case 0:
      return RealScalar.ONE;
    case 1:
      return Mean.of(distribution).zero();
    case 2:
      return Variance.of(distribution);
    default:
      throw new UnsupportedOperationException();
    }
  }
}
