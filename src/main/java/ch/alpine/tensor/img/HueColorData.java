// code by jph
package ch.alpine.tensor.img;

import ch.alpine.tensor.DeterminateScalarQ;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Clips;

/* package */ class HueColorData implements ColorDataGradient {
  public static final ColorDataGradient DEFAULT = new HueColorData(1.0);
  // ---
  private final double opacity;

  private HueColorData(double opacity) {
    this.opacity = opacity;
  }

  @Override // from ScalarTensorFunction
  public Tensor apply(Scalar scalar) {
    return DeterminateScalarQ.of(scalar) //
        ? ColorFormat.toVector(Hue.of(scalar.number().doubleValue(), 1, 1, opacity))
        : Transparent.rgba();
  }

  @Override // from ColorDataGradient
  public ColorDataGradient deriveWithOpacity(Scalar opacity) {
    return new HueColorData(Clips.unit().requireInside(opacity).number().doubleValue());
  }
}
