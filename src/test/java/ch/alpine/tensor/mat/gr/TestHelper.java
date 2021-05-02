// code by jph
package ch.alpine.tensor.mat.gr;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.Unit;

/* package */ enum TestHelper {
  ;
  public static void requireNonQuantity(Tensor u) {
    if (u.flatten(-1).anyMatch(scalar -> scalar instanceof Quantity))
      throw TensorRuntimeException.of(u);
  }

  public static void requireUnit(Tensor values, Unit unit) {
    if (!values.flatten(-1) //
        .map(Quantity.class::cast) //
        .map(Quantity::unit) //
        .allMatch(unit::equals))
      throw TensorRuntimeException.of(values);
  }
}
