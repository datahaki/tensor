// code by jph
package ch.alpine.tensor.pdf;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.Tensor;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/RandomChoice.html">RandomChoice</a> */
public enum RandomChoice {
  ;
  /** @param tensor
   * @param randomGenerator
   * @return random entry of tensor
   * @throws Exception if given tensor is empty */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> T of(Tensor tensor, RandomGenerator randomGenerator) {
    return (T) tensor.get(randomGenerator.nextInt(tensor.length()));
  }

  /** @param tensor
   * @return random entry of tensor
   * @throws Exception if given tensor is empty */
  public static <T extends Tensor> T of(Tensor tensor) {
    return of(tensor, ThreadLocalRandom.current());
  }

  // ---
  /** @param list
   * @param randomGenerator
   * @return random entry of list
   * @throws Exception if given list is empty */
  public static <T> T of(List<T> list, RandomGenerator randomGenerator) {
    return list.get(randomGenerator.nextInt(list.size()));
  }

  /** @param list
   * @return random entry of list */
  public static <T> T of(List<T> list) {
    return of(list, ThreadLocalRandom.current());
  }
}
