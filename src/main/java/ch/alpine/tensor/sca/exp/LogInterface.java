// code by jph
package ch.alpine.tensor.sca.exp;

import ch.alpine.tensor.Scalar;

/** interface may be implemented by {@link Scalar} to support
 * the computation of the logarithmic value in {@link Log} */
@FunctionalInterface
public interface LogInterface {
  /** @return scalar that satisfies scalar == Log[this] */
  Scalar log();
}
