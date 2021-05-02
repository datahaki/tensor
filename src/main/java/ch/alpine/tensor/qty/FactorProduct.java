// code by jph
package ch.alpine.tensor.qty;

import java.io.Serializable;

import ch.alpine.tensor.Scalar;

/* package */ class FactorProduct implements Factor, Serializable {
  private final Scalar scalar;
  private final Unit base;

  /** @param scalar may or may not be of instance {@link Quantity} */
  public FactorProduct(Scalar scalar) {
    this.scalar = scalar;
    base = QuantityUnit.of(scalar);
  }

  @Override
  public Scalar times(Quantity quantity) {
    return quantity.value().multiply(scalar);
  }

  @Override
  public Unit dimensions(Unit unit) {
    return base;
  }
}