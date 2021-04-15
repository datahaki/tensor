// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum TestHelper {
  ;
  public static void requireNonQuantity(Tensor u) {
    if (u.flatten(-1).anyMatch(scalar -> scalar instanceof Quantity))
      throw TensorRuntimeException.of(u);
  }
}
