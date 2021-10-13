// code by jph
package ch.alpine.tensor.lie.ad;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Chop;

public enum JacobiIdentity {
  ;
  /** @param ad tensor of Lie-algebra
   * @return 4-dimensional array of all zeros if ad corresponds to a Lie-algebra */
  public static Tensor of(Tensor ad) {
    return BianchiIdentity.of(ad.dot(ad));
  }

  /** @param ad tensor of Lie-algebra
   * @param chop
   * @return */
  public static Tensor require(Tensor ad, Chop chop) {
    chop.requireAllZero(of(ad));
    return ad;
  }

  /** @param ad tensor of Lie-algebra
   * @return */
  public static Tensor require(Tensor ad) {
    return require(ad, Tolerance.CHOP);
  }
}
