// code by jph
package ch.alpine.tensor.qty;

import java.io.Serializable;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;

public record UnitDimensionsBase(UnitSystem unitSystem) implements UnitDimensions, Serializable {
  public static final UnitDimensions SI = new UnitDimensionsBase(UnitSystem.SI());

  @Override
  public Scalar normalForm(Scalar scalar) {
    return unitSystem.apply(scalar);
  }

  @Override
  public Scalar plus(Scalar a, Scalar b) {
    if (b instanceof DateTime)
      return b.add(a);
    // TODO this can be improved, eg kW and MW could be compiled into kW
    Scalar qa = normalForm(a);
    Scalar qb = normalForm(b);
    if (QuantityUnit.of(qa).equals(QuantityUnit.of(qb)))
      return qa.add(qb);
    throw new Throw(a, b);
  }
}
