// code by jph
package ch.alpine.tensor.img;

import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** fallback color data gradient is used when loading the
 * resource associated to a color data gradient fails. */
/* package */ enum FallbackColorDataGradient implements ColorDataGradient {
  INSTANCE;

  @Override // from ColorDataGradient
  public Tensor apply(Scalar scalar) {
    Objects.requireNonNull(scalar);
    return Transparent.rgba();
  }

  @Override // from ColorDataGradient
  public ColorDataGradient deriveWithOpacity(Scalar factor) {
    Objects.requireNonNull(factor);
    return this;
  }
}
