// code by jph
package ch.alpine.tensor.spa;

import java.util.List;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;

public enum SparseArrays {
  ;
  /** @param tensor with array structure
   * @return
   * @throws Exception if given tensor does not have array structure */
  public static Tensor of(Tensor tensor) {
    Dimensions dimensions = new Dimensions(tensor);
    List<Integer> size = dimensions.list();
    if (0 == size.size()) // tensor is a scalar
      return tensor;
    Scalar fallback = tensor.flatten(-1).map(Scalar.class::cast).limit(1).findFirst().get().zero();
    return StaticHelper.of(fallback, size, tensor);
  }
}
