// code by jph
package ch.alpine.tensor.mat.sv;

import ch.alpine.tensor.Scalar;

/* package */ class Rotate {
  private final Scalar re;
  private final Scalar im;

  /** emulation of complex multiplication (x + y*i) (c + s*i)
   * 
   * @param x
   * @param y
   * @param c
   * @param s */
  public Rotate(Scalar x, Scalar y, Scalar c, Scalar s) {
    re = x.multiply(c).subtract(y.multiply(s));
    im = y.multiply(c).add(x.multiply(s));
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
