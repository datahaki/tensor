// code by jph
package ch.alpine.tensor.red;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.pdf.CentralMomentInterface;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.sca.Power;

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

  /** @param distribution
   * @param order
   * @return central moment of given distribution and order */
  public static Scalar of(Distribution distribution, int order) {
    if (distribution instanceof CentralMomentInterface centralMomentInterface)
      return centralMomentInterface.centralMoment(order);
    return switch (order) {
    case 0 -> RealScalar.ONE;
    case 1 -> Mean.of(distribution).zero();
    case 2 -> Variance.of(distribution);
    default -> throw new UnsupportedOperationException();
    };
  }
}
