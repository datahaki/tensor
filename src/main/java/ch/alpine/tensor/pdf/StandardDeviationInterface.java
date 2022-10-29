// code by jph
package ch.alpine.tensor.pdf;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.red.StandardDeviation;

/** @see StandardDeviation */
@FunctionalInterface
public interface StandardDeviationInterface {
  /** @return standard deviation */
  Scalar standardDeviation();
}
