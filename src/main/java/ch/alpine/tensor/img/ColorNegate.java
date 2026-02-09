// code by jph
package ch.alpine.tensor.img;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.TensorMap;
import ch.alpine.tensor.mat.MatrixQ;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/ColorNegate.html">ColorNegate</a> */
public enum ColorNegate {
  ;
  /** @param tensor matrix, or with dimensions n x m x 4
   * @return */
  public static Tensor of(Tensor tensor) {
    return tensor.get(0, 0) instanceof Scalar //
        ? MatrixQ.require(tensor).maps(s -> StaticHelper.LOOKUP[255 - s.number().intValue()])
        : TensorMap.of(v -> Tensors.of( //
            StaticHelper.LOOKUP[255 - v.Get(0).number().intValue()], //
            StaticHelper.LOOKUP[255 - v.Get(1).number().intValue()], //
            StaticHelper.LOOKUP[255 - v.Get(2).number().intValue()], //
            v.Get(3)), tensor, 2);
  }
}
