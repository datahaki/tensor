// code by jph
package ch.alpine.tensor.api;

import ch.alpine.tensor.Tensor;

public enum Slash {
  ;
  /** @param tuo
   * @param tensor
   * @return tuo /@ tensor */
  public static Tensor of(TensorUnaryOperator tensorUnaryOperator, Tensor tensor) {
    return tensorUnaryOperator.slash(tensor);
  }
}
