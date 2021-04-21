// code by jph
package ch.ethz.idsc.tensor.opt.lp;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Sign;

/* package */ enum StaticHelper {
  ;
  /** @param vector
   * @return true if all entries in vector are non-negative */
  /* package */ static boolean isNonNegative(Tensor vector) {
    return vector.stream().map(Scalar.class::cast).allMatch(Sign::isPositiveOrZero);
  }
}
