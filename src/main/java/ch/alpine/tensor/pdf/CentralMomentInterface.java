// code by jph
package ch.alpine.tensor.pdf;

import ch.alpine.tensor.Scalar;

@FunctionalInterface
public interface CentralMomentInterface {
  /** @param order
   * @return central moment of given order */
  Scalar centralMoment(int order);
}
