// code by jph
package ch.alpine.tensor.mat.sv;

import ch.alpine.tensor.Scalar;

/** complex rotation (x + y*i) (c + s*i) */
/* package */ class Rotate {
  private final Scalar re;
  private final Scalar im;

  /** @param x
   * @param y
   * @param c
   * @param s */
  public Rotate(Scalar x, Scalar y, Scalar c, Scalar s) {
    re = x.multiply(c).subtract(y.multiply(s));
    im = y.multiply(c).add(x.multiply(s));
  }

  /** @return y*c - x*s */
  public Scalar re() {
    return re;
  }

  /** @return x*c + y*s */
  public Scalar im() {
    return im;
  }
}
