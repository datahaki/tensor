// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/CoprimeQ.html">CoprimeQ</a> */
public enum CoprimeQ {
  ;
  /** @param a
   * @param b
   * @return */
  public static boolean of(Scalar a, Scalar b) {
    Scalar c = StaticHelper.normalForm(a.multiply(b));
    return Scalars.isZero(a) && Scalars.isZero(b) //
        ? false
        : LCM.of(a, b).equals(c);
  }
}
