// code by jph
package ch.alpine.tensor.api;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Arg;

/** interface may be implemented by {@link Scalar}
 * to support the computation of the complex argument in {@link Arg} */
@FunctionalInterface
public interface ArgInterface {
  /** @return argument of this number in the complex plane */
  Scalar arg();
}
