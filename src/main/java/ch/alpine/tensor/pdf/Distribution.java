// code by jph
package ch.alpine.tensor.pdf;

import java.util.random.RandomGenerator;

import ch.alpine.tensor.Scalar;

/** base interface for a univariate probability distribution
 * capability to produce random variate
 * 
 * <p>An instance of {@link Distribution} is immutable. */
public interface Distribution {
  /** @param randomGenerator
   * @return sample generated using the given random generator */
  Scalar randomVariate(RandomGenerator randomGenerator);
}
