// code by jph
package ch.alpine.tensor.spa;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/SparseArrayQ.html">SparseArrayQ</a> */
public enum SparseArrayQ {
  ;
  /** @param tensor
   * @return true if given tensor is instance of {@link SparseArray} */
  public static boolean of(Tensor tensor) {
    return tensor instanceof SparseArray;
  }

  /** @param tensor
   * @return given tensor
   * @throws Exception if given tensor does not have all entries in exact precision */
  public static Tensor require(Tensor tensor) {
    if (of(tensor))
      return tensor;
    throw Throw.of(tensor);
  }
}
