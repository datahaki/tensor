// code by jph
package ch.alpine.tensor.opt.lp;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Sign;

/* package */ enum StaticHelper {
  ;
  /** @param vector
   * @return true if all entries in vector are non-negative */
  public static boolean isNonNegative(Tensor vector) {
    return vector.stream().map(Scalar.class::cast).allMatch(Sign::isPositiveOrZero);
  }

  /** @param ind
   * @param n
   * @return whether all ind < n */
  public static boolean isInsideRange(Tensor ind, int n) {
    return ind.stream() //
        .map(Scalar.class::cast) //
        .map(Scalars::intValueExact) //
        .allMatch(i -> i < n);
  }
}
