// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.sca.Chop;

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
