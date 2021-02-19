// code by jph
package ch.ethz.idsc.tensor.api;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Exp;

/** interface may be implemented by {@link Scalar} to support
 * the computation of the exponential value in {@link Exp} */
@FunctionalInterface
public interface ExpInterface {
  /** @return scalar that satisfies scalar == Exp[this] */
  Scalar exp();
}
