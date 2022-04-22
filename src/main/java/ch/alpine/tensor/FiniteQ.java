// code by jph
package ch.alpine.tensor;

import java.util.Objects;

import ch.alpine.tensor.api.InexactScalarMarker;

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
public enum FiniteQ {
  ;
  /** @param scalar
   * @return true otherwise true */
  public static boolean of(Scalar scalar) {
    if (scalar instanceof InexactScalarMarker inexactScalarMarker)
      return inexactScalarMarker.isFinite();
    if (scalar instanceof MultiplexScalar multiplexScalar)
      return multiplexScalar.allMatch(FiniteQ::of);
    Objects.requireNonNull(scalar);
    return true;
  }

  /** @param tensor
   * @return true, if all scalar entries in given tensor are finite */
  public static boolean all(Tensor tensor) {
    return tensor.flatten(-1).map(Scalar.class::cast).allMatch(FiniteQ::of);
  }
}
