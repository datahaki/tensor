// code by jph
package ch.alpine.tensor.chq;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;

/** Hint:
 * use the instanceof predicate to determine whether an object is instance of {@link Scalar} */
public enum ScalarQ {
  ;
  /** @param tensor
   * @throws Exception if given tensor is an instance of {@link Scalar} */
  public static void thenThrow(Tensor tensor) {
    if (tensor instanceof Scalar)
      throw TensorRuntimeException.of(tensor);
  }
}