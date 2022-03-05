// code by jph
package ch.alpine.tensor.sca.tri;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** SinhcInverse[z] := z / Sinh[z] */
public enum SinhcInverse implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar z) {
    Scalar value = Sinh.FUNCTION.apply(z);
    return Scalars.isZero(z) //
        ? z.one()
        : z.divide(value);
  }
}
