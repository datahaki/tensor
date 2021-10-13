// code by jph
package ch.alpine.tensor;

import ch.alpine.tensor.alg.ArrayReshape;
import ch.alpine.tensor.alg.Dimensions;

/** Mathematica::Normal[3] == 3
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Normal.html">Normal</a> */
/* package */ enum Normal {
  ;
  public static Tensor of(Tensor tensor) {
    if (ScalarQ.of(tensor))
      return tensor;
    Dimensions dimensions = new Dimensions(tensor);
    return dimensions.isArray() //
        ? ArrayReshape.of(tensor, dimensions.list())
        : tensor.copy();
  }
}
