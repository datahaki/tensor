// code by jph
package ch.alpine.tensor.api;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Log;

/** interface may be implemented by {@link Scalar} to support
 * the computation of the logarithmic value in {@link Log} */
@FunctionalInterface
public interface LogInterface {
  /** @return scalar that satisfies scalar == Log[this] */
  Scalar log();
}
