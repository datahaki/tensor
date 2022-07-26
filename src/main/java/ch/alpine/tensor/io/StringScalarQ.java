// code by jph
package ch.alpine.tensor.io;

import ch.alpine.tensor.Tensor;

public enum StringScalarQ {
  ;
  /** @param tensor
   * @return true if any scalar entries in given tensor is an instance of {@link StringScalar} */
  public static boolean any(Tensor tensor) {
    return tensor.flatten(-1).anyMatch(StringScalar.class::isInstance);
  }
}
