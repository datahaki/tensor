// code by jph
package ch.alpine.tensor.img;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.red.Entrywise;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/MaxFilter.html">MaxFilter</a> */
public enum MaxFilter {
  ;
  /** @param tensor
   * @param radius
   * @return */
  public static Tensor of(Tensor tensor, int radius) {
    return TensorExtract.of(tensor, radius, Entrywise.max()::of);
  }
}
