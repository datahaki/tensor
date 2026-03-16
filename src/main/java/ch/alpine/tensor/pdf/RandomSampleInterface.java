// code by jph
package ch.alpine.tensor.pdf;

import java.io.Serializable;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;

/** RandomSampleInterface produces tensors from a multi-variate probability distribution. */
@FunctionalInterface
public interface RandomSampleInterface extends Serializable {
  /** @return randomGenerator sample from continuous or discrete set */
  Tensor randomSample(RandomGenerator randomGenerator);

  /** @param tuo
   * @return tuo.apply(random) */
  default RandomSampleInterface andThen(TensorUnaryOperator tuo) {
    return randomGenerator -> tuo.apply(randomSample(randomGenerator));
  }
}
