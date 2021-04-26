// code by jph
package ch.ethz.idsc.tensor.mat.gr;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;

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
