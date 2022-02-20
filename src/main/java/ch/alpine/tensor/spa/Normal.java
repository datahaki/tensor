// code by jph
package ch.alpine.tensor.spa;

import ch.alpine.tensor.ScalarQ;
import ch.alpine.tensor.Tensor;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Normal.html">Normal</a> */
public enum Normal {
  ;
  /** Converts {@link SparseArray} to full tensor
   * 
   * <p>Special case:
   * Mathematica::Normal[3] == 3
   * 
   * @param tensor
   * @return */
  public static Tensor of(Tensor tensor) {
    return ScalarQ.of(tensor) //
        ? tensor
        : Tensor.of(tensor.stream().map(Normal::of));
  }
}
