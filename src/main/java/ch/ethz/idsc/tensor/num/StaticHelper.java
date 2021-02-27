// code by jph
package ch.ethz.idsc.tensor.num;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Abs;

/* package */ enum StaticHelper {
  ;
  // LONGTERM function does not result in Mathematica standard for all input
  public static Scalar normalForm(Scalar scalar) {
    if (scalar instanceof RealScalar)
      return Abs.FUNCTION.apply(scalar);
    return scalar;
  }
}
