// code by jph
package ch.alpine.tensor.pdf;

import java.util.Arrays;
import java.util.stream.Stream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.num.RandomPermutation;

public enum TensorShuffle {
  ;
  /** @param tensor
   * @return random permutation of entries of tensor */
  public static Tensor of(Tensor tensor) {
    return Tensor.of(stream(tensor));
  }

  /** @param tensor
   * @return stream of entries of tensor in random order */
  public static Stream<Tensor> stream(Tensor tensor) {
    return Arrays.stream(RandomPermutation.of(tensor.length())).mapToObj(tensor::get);
  }
}
