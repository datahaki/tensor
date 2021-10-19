// code by jph
package ch.alpine.tensor.spa;

import ch.alpine.tensor.ScalarQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.ArrayReshape;
import ch.alpine.tensor.alg.Dimensions;

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
    if (ScalarQ.of(tensor))
      return tensor;
    Dimensions dimensions = new Dimensions(tensor);
    return dimensions.isArray() //
        ? ArrayReshape.of(tensor, dimensions.list())
        : tensor.copy();
  }
}
