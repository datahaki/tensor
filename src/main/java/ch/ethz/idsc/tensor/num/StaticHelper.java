// code by jph
package ch.ethz.idsc.tensor.num;

import java.util.Collections;
import java.util.Map;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.ext.LruCache;
import ch.ethz.idsc.tensor.sca.Abs;

/* package */ enum StaticHelper {
  ;
  /** stores sqrt roots of gauss scalars */
  public static final Map<GaussScalar, GaussScalar> SQRT = //
      Collections.synchronizedMap(new LruCache<>(256 * 3));

  // LONGTERM function does not result in Mathematica standard for all input
  public static Scalar normalForm(Scalar scalar) {
    if (scalar instanceof RealScalar)
      return Abs.FUNCTION.apply(scalar);
    return scalar;
  }
}
