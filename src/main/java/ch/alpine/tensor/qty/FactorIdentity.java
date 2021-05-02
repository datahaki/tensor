// code by jph
package ch.alpine.tensor.qty;

import ch.alpine.tensor.Scalar;

/* package */ enum FactorIdentity implements Factor {
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