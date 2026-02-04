// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Abs;

/** reviewed */
/* package */ enum StaticHelper {
  ;
  // TODO TENSOR NUM function does not result in Mathematica standard for all input
  public static Scalar normalForm(Scalar scalar) {
    return scalar instanceof RealScalar //
        ? Abs.FUNCTION.apply(scalar)
        : scalar;
  }
}
