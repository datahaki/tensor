// code by jph
package ch.alpine.tensor.api;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.MachineNumberQ;
import ch.alpine.tensor.Scalar;

/** a {@link Scalar} may implement the interface to signal that the value is in machine precision.
 * For example, a {@link DoubleScalar} implements the function isMachineNumber() to return true.
 * 
 * <p>a {@link Scalar} that does not implement {@link MachineNumberQInterface} is assumed to
 * not represent an exact quantity by {@link MachineNumberQ}. */
@FunctionalInterface
public interface MachineNumberQInterface {
  /** @return true, if scalar is encoded in exact precision */
  boolean isMachineNumber();
}
