// code by jph
package ch.ethz.idsc.tensor.red;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.nrm.Norm1;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/MeanDeviation.html">MeanDeviation</a> */
public enum MeanDeviation {
  ;
  /** @param vector with at least one entry
   * @return mean deviation of entries in given vector
   * @throws Exception if input is not a vector, or is empty */
  public static Scalar ofVector(Tensor vector) {
    Scalar nmean = (Scalar) Mean.of(vector).negate();
    return Norm1.ofVector(vector.stream().map(nmean::add)) //
        .divide(RealScalar.of(vector.length()));
  }
}
