// code by jph
package ch.alpine.tensor.chq;

import ch.alpine.tensor.MultiplexScalar;
import ch.alpine.tensor.Scalar;

/** <pre>
 * DeterminateScalarQ[ +Infinity ] == true
 * DeterminateScalarQ[ -Infinity ] == true
 * DeterminateScalarQ[ NaN ] == false
 * </pre> */
public enum DeterminateScalarQ {
  ;
  /** @param scalar
   * @return true if any component of scalar is not itself */
  public static boolean of(Scalar scalar) {
    if (scalar instanceof MultiplexScalar) {
      MultiplexScalar multiplexScalar = (MultiplexScalar) scalar;
      return multiplexScalar.allMatch(DeterminateScalarQ::of);
    }
    return scalar.equals(scalar);
  }
}
