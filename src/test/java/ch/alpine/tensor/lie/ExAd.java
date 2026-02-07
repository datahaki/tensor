// code by jph
package ch.alpine.tensor.lie;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.Import;

public enum ExAd {
  SO3(LeviCivitaTensor.of(3).negate()),
  SE2("{{{0, 0, 0}, {0, 0, -1}, {0, 1, 0}}, {{0, 0, 1}, {0, 0, 0}, {-1, 0, 0}}, {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}}"),
  SE3(Import.of("/ch/alpine/tensor/io/se3_ad.mathematica")),
  SL2("{{{0, 0, 0}, {0, 0, 1}, {0, -1, 0}}, {{0, 0, 1}, {0, 0, 0}, {-1, 0, 0}}, {{0, -1, 0}, {1, 0, 0}, {0, 0, 0}}}"),
  HE1("{{{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}, {{0, 0, -1}, {0, 0, 0}, {1, 0, 0}}, {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}}"),
  HE2(Import.of("/ch/alpine/tensor/io/he2_ad.mathematica")), //
  ;

  private final Tensor ad;

  ExAd(Tensor ad) {
    this.ad = ad.unmodifiable();
  }

  ExAd(String string) {
    this(Tensors.fromString(string));
  }

  public Tensor ad() {
    return ad;
  }
}
