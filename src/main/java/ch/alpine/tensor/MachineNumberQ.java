// code by jph
package ch.alpine.tensor;

import java.util.Objects;

import ch.alpine.tensor.api.MachineNumberQInterface;

/** implementation consistent with Mathematica
 * <pre>
 * MachineNumberQ[ 3.14 + 2.7*I ] == true
 * MachineNumberQ[ 13 / 17 ] == false
 * </pre>
 * 
 * <p>Special cases are
 * <pre>
 * MachineNumberQ[ Infinity ] == false
 * MachineNumberQ[ Indeterminate ] == false
 * MachineNumberQ[ 3.0[m] ] == false
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/MachineNumberQ.html">MachineNumberQ</a>
 * 
 * @see ExactScalarQ */
@Deprecated
public enum MachineNumberQ {
  ;
  /** @param scalar
   * @return true, if scalar is instance of {@link MachineNumberQInterface} which evaluates to true,
   * otherwise false */
  public static boolean of(Scalar scalar) {
    if (scalar instanceof MachineNumberQInterface machineNumberQInterface)
      return machineNumberQInterface.isMachineNumber();
    if (scalar instanceof MultiplexScalar multiplexScalar)
      return multiplexScalar.allMatch(MachineNumberQ::of);
    Objects.requireNonNull(scalar);
    return false;
  }

  /** @param tensor
   * @return true, if any scalar entry in given tensor satisfies {@link #of(Scalar)} predicate */
  public static boolean any(Tensor tensor) {
    return tensor.flatten(-1).map(Scalar.class::cast).anyMatch(MachineNumberQ::of);
  }
}
