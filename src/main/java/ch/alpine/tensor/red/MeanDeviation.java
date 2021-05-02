// code by jph
package ch.alpine.tensor.red;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.nrm.Vector1Norm;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/MeanDeviation.html">MeanDeviation</a> */
public enum MeanDeviation {
  ;
  /** @param vector with at least one entry
   * @return mean deviation of entries in given vector
   * @throws Exception if input is not a vector, or is empty */
  public static Scalar ofVector(Tensor vector) {
    Scalar nmean = (Scalar) Mean.of(vector).negate();
    return Vector1Norm.of(vector.stream().map(nmean::add)).divide(RealScalar.of(vector.length()));
  }
}
