// code by jph
package ch.alpine.tensor.sca.exp;

import ch.alpine.tensor.Scalar;

/** interface may be implemented by {@link Scalar} to support
 * the computation of the exponential value in {@link Exp} */
@FunctionalInterface
public interface ExpInterface {
  /** @return scalar that satisfies scalar == Exp[this] */
  Scalar exp();
}
