// code by jph
package ch.ethz.idsc.tensor.api;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Log;

/** interface may be implemented by {@link Scalar} to support
 * the computation of the logarithmic value in {@link Log} */
@FunctionalInterface
public interface LogInterface {
  /** @return scalar that satisfies scalar == Log[this] */
  Scalar log();
}
