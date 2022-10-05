// code by jph
package ch.alpine.tensor.chq;

import ch.alpine.tensor.MultiplexScalar;
import ch.alpine.tensor.Scalar;

/** <pre>
 * IndeterminateQ[ +Infinity ] == false
 * IndeterminateQ[ -Infinity ] == false
 * IndeterminateQ[ NaN ] == true
 * </pre> */
public enum IndeterminateQ {
  ;
  /** @param scalar
   * @return true if any component of scalar is not itself */
  public static boolean of(Scalar scalar) {
    if (scalar instanceof MultiplexScalar multiplexScalar)
      return multiplexScalar.allMatch(FiniteScalarQ::of);
    return !scalar.equals(scalar);
  }
}
