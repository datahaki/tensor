// code by jph
package ch.alpine.tensor.qty;

import java.io.Serializable;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;

public record SiUnitDimensions(UnitSystem unitSystem) implements UnitDimensions, Serializable {
  public static final UnitDimensions SI = new SiUnitDimensions(UnitSystem.SI());

  @Override
  public Scalar normalForm(Scalar scalar) {
    return unitSystem.apply(scalar);
  }

  @Override
  public Scalar plus(Scalar a, Scalar b) {
    Scalar qa = unitSystem.apply(a);
    Scalar qb = unitSystem.apply(b);
    if (QuantityUnit.of(qa).equals(QuantityUnit.of(qb)))
      return qa.add(qb);
    throw new Throw(a, b);
  }
}
