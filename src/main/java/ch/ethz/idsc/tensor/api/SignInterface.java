// code by jph
package ch.ethz.idsc.tensor.api;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Sign;

/** a {@link Scalar} implements SignInterface if the signum of the scalar type
 * is a meaningful operation. The interface is used in {@link Sign}. */
public interface SignInterface {
  /** @return the "direction" of the scalar, or zero if scalar is zero */
  Scalar sign();
}
