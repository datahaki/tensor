// code by jph
package ch.alpine.tensor.pdf;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.red.StandardDeviation;

/** @implSpec function is implemented if precision gain over Sqrt[Variance] is expected
 * 
 * @see StandardDeviation */
@FunctionalInterface
public interface StandardDeviationInterface {
  /** @return standard deviation */
  Scalar standardDeviation();
}
