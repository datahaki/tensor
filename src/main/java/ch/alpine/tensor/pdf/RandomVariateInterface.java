// code by jph
package ch.alpine.tensor.pdf;

import java.util.random.RandomGenerator;

import ch.alpine.tensor.Scalar;

/** capability to produce random variate */
@FunctionalInterface
public interface RandomVariateInterface {
  /** @param random
   * @return sample generated using the given random generator */
  Scalar randomVariate(RandomGenerator random);
}
