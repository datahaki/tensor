// code by jph
package ch.alpine.tensor.api;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.ArcTan;

/** when the computation cannot be carried out, the implementation should return
 * <pre>
 * ArcTan.FUNCTION.apply(divide(x))
 * </pre>
 * 
 * Mathematica::ArcTan[x, y]
 * 
 * @see ArcTan */
@FunctionalInterface
public interface ArcTanInterface {
  /** @param x
   * @return ArcTan[x, this] == ArcTan[ this / x ] */
  Scalar arcTan(Scalar x);
}
