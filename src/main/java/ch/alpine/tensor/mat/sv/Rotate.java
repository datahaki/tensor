// code by jph
package ch.alpine.tensor.mat.sv;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Unprotect;

/** complex rotation (x + y*i) (c + s*i) */
/* package */ class Rotate {
  private final Scalar re;
  private final Scalar im;

  /** @param x
   * @param y
   * @param c
   * @param s */
  public Rotate(Scalar x, Scalar y, Scalar c, Scalar s) {
    re = Unprotect.zeroProject(x.multiply(c)).subtract(Unprotect.zeroProject(y.multiply(s)));
    im = Unprotect.zeroProject(y.multiply(c)).add(Unprotect.zeroProject(x.multiply(s)));
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
