// code by jph
package ch.alpine.tensor.pdf;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.red.CentralMoment;

/** implementations may assume that order is non-negative
 * 
 * @see CentralMoment
 * @see VarianceInterface */
@FunctionalInterface
public interface CentralMomentInterface {
  /** @param order non-negative
   * @return central moment of given order */
  Scalar centralMoment(int order);
}
