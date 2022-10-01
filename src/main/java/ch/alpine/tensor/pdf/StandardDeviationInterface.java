// code by jph
package ch.alpine.tensor.pdf;

import ch.alpine.tensor.Scalar;

// TODO TENSOR IMPL for more distributions to gain precision
@FunctionalInterface
public interface StandardDeviationInterface {
  /** @return standard deviation */
  Scalar standardDeviation();
}
