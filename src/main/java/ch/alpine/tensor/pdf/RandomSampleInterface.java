// code by jph
package ch.alpine.tensor.pdf;

import java.util.random.RandomGenerator;

import ch.alpine.tensor.Tensor;

/** RandomSampleInterface produces tensors from a multi-variate probability distribution. */
@FunctionalInterface
public interface RandomSampleInterface {
  /** @return randomGenerator sample from continuous or discrete set */
  Tensor randomSample(RandomGenerator randomGenerator);
}
