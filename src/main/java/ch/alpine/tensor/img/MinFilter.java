// code by jph
package ch.alpine.tensor.img;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.red.Entrywise;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/MinFilter.html">MinFilter</a> */
public enum MinFilter {
  ;
  /** @param tensor
   * @param radius
   * @return */
  public static Tensor of(Tensor tensor, int radius) {
    return TensorExtract.of(Unprotect.references(tensor), radius, Entrywise.min()::of);
  }
}
