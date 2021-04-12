// code by jph
package ch.ethz.idsc.tensor.qty;

import ch.ethz.idsc.tensor.Scalar;

/* package */ enum FactorEmpty implements Factor {
  INSTANCE;

  @Override
  public Scalar times(Quantity quantity) {
    return quantity;
  }

  @Override
  public Unit dimensions(Unit unit) {
    return unit;
  }
}