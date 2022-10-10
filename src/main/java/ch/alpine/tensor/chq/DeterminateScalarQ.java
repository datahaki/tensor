// code by jph
package ch.alpine.tensor.chq;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;

/** Example usage:
 * {@link Scalars#compare(Scalar, Scalar)} throws an Exception
 * if either scalar is NaN. So if that can't be rules out, the
 * comparison should be preceeded by a check that handles the
 * indeterminate separately.
 * 
 * Remark:
 * Mathematica uses "Indeterminate" in place of "Not a Number" NaN
 * From that, we derive the name for "not indeterminate" as "determinate". */
public enum DeterminateScalarQ {
  ;
  /** <pre>
   * DeterminateScalarQ[ 0 ] == true
   * DeterminateScalarQ[ 2 + 3 * I ] == true
   * DeterminateScalarQ[ +Infinity ] == true
   * DeterminateScalarQ[ -Infinity ] == true
   * </pre>
   * 
   * <pre>
   * DeterminateScalarQ[ NaN ] == false
   * DeterminateScalarQ[ 2 + NaN * I ] == false
   * DeterminateScalarQ[ NaN + 3 * I ] == false
   * </pre>
   * 
   * @param scalar
   * @return whether given scalar is equal to itself */
  public static boolean of(Scalar scalar) {
    return scalar.equals(scalar);
  }
}
