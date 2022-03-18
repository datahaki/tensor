// code by jph
package ch.alpine.tensor.sca.tri;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** Sinhc[z] := Sinh[z] / z
 * 
 * <pre>
 * Sinhc[NaN] == NaN
 * </pre> */
public enum Sinhc implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar z) {
    Scalar value = Sinh.FUNCTION.apply(z);
    return Scalars.isZero(z) //
        ? z.one() //
        : value.divide(z);
  }
}
