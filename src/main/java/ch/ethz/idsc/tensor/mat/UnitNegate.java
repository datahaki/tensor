// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum UnitNegate implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof Quantity) {
      Quantity quantity = (Quantity) scalar;
      return Quantity.of(quantity.value(), quantity.unit().negate());
    }
    return scalar;
  }
}
