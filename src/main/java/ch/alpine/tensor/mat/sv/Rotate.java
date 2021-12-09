// code by jph
package ch.alpine.tensor.mat.sv;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.red.LenientAdd;

/** complex rotation (x + y*i) (c + s*i) */
/* package */ class Rotate {
  private final Scalar re;
  private final Scalar im;

  /** @param x
   * @param y
   * @param c without unit
   * @param s without unit */
  public Rotate(Scalar x, Scalar y, Scalar c, Scalar s) {
    // y = Unprotect.zeroDropUnit(y);
    // re = x.multiply(c).subtract(y.multiply(s));
    // im = y.multiply(c).add(x.multiply(s));
    re = LenientAdd.of(x.multiply(c), y.multiply(s).negate());
    im = LenientAdd.of(y.multiply(c), x.multiply(s));
  }

  /** @return x*c + y*s */
  public Scalar re() {
    return re;
  }

  /** @return y*c - x*s */
  public Scalar im() {
    return im;
  }
}
