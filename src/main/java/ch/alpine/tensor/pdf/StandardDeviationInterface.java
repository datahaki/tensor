// code by jph
package ch.alpine.tensor.pdf;

import ch.alpine.tensor.Scalar;

@FunctionalInterface
public interface StandardDeviationInterface {
  /** @return standard deviation */
  Scalar standardDeviation();
}
