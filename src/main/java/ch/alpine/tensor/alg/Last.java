// code by jph
package ch.alpine.tensor.alg;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Last.html">Last</a> */
public enum Last {
  ;
  /** @param tensor of rank at least 1, non-empty
   * @return last entry of tensor
   * @throws Exception if tensor is empty
   * @throws Exception if tensor is a {@link Scalar} */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> T of(Tensor tensor) {
    return (T) tensor.get(tensor.length() - 1);
  }
}
