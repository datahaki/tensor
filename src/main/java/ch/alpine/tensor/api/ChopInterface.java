// code by jph
package ch.alpine.tensor.api;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Chop;

/** interface may be implemented by {@link Scalar}
 * to support the chop towards zero function
 * 
 * the interface is optional: the identity function is
 * used if the interface is not implemented. */
@FunctionalInterface
public interface ChopInterface {
  /** @param chop
   * @return {@link Scalar#zero()} if Scalar has numeric precision and
   * absolute value is strictly below threshold defined by {@link Chop#threshold()} */
  Scalar chop(Chop chop);
}
