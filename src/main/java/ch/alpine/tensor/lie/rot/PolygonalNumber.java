// code by jph
package ch.alpine.tensor.lie.rot;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/PolygonalNumber.html">PolygonalNumber</a> */
public enum PolygonalNumber {
  ;
  private static final Scalar _4 = RealScalar.of(4);

  /** @param n
   * @return 1 + 2 + ... + n == n (n + 1) / 2 */
  public static Scalar of(Scalar n) {
    return n.multiply(n.add(RealScalar.ONE)).multiply(Rational.HALF);
  }

  /** @param r
   * @param n
   * @return 1/2 n (n (r-2)-r+4) */
  public static Scalar of(Scalar r, Scalar n) {
    Scalar v = r.subtract(RealScalar.TWO).multiply(n).subtract(r).add(_4);
    return n.multiply(v).multiply(Rational.HALF);
  }
}
