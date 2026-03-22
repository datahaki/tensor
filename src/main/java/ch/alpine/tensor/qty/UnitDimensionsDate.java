// code by jph
package ch.alpine.tensor.qty;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;

public enum UnitDimensionsDate implements UnitDimensions {
  INSTANCE;

  @Override
  public Scalar normalForm(Scalar scalar) {
    return scalar;
  }

  @Override
  public Scalar strip(Scalar scalar) {
    throw new Throw(scalar);
  }

  @Override
  public Scalar plus(Scalar a, Scalar b) {
    if (b instanceof DateTime)
      return b.add(a);
    throw new Throw(a, b);
  }
}
