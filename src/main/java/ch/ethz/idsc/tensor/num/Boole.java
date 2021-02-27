// code by jph
package ch.ethz.idsc.tensor.num;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Boole.html">Boole</a> */
public enum Boole {
  ;
  /** Hint: for the conversion to boolean use for instance
   * {@link Scalars#nonZero(Scalar)}, or {@link Scalars#isZero(Scalar)}
   * 
   * @param value
   * @return 1 if given value is true, else 0 */
  public static Scalar of(boolean value) {
    return value //
        ? RealScalar.ONE
        : RealScalar.ZERO;
  }
}
