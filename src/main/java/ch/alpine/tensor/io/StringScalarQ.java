// code by jph
package ch.alpine.tensor.io;

import ch.alpine.tensor.Tensor;

public enum StringScalarQ {
  ;
  /** @param tensor
   * @return true if any scalar entries in given tensor satisfies the predicate {@link StringScalarQ#of(Tensor)} */
  public static boolean any(Tensor tensor) {
    return tensor.flatten(-1).anyMatch(StringScalar.class::isInstance);
  }
}
