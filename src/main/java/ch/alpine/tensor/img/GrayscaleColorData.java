// code by jph
package ch.alpine.tensor.img;

import ch.alpine.tensor.DeterminateScalarQ;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.Clips;

/* package */ class GrayscaleColorData implements ColorDataGradient {
  public static final ColorDataGradient DEFAULT = new GrayscaleColorData(255);
  // ---
  private final Tensor[] tensors = new Tensor[256];

  private GrayscaleColorData(int alpha) {
    for (int index = 0; index < 256; ++index)
      tensors[index] = Tensors.vectorDouble(index, index, index, alpha);
  }

  @Override // from ScalarTensorFunction
  public Tensor apply(Scalar scalar) {
    return DeterminateScalarQ.of(scalar) //
        ? tensors[toInt(scalar.number().doubleValue())].copy()
        : Transparent.rgba();
  }

  @Override // from ColorDataGradient
  public ColorDataGradient deriveWithOpacity(Scalar opacity) {
    double value = Clips.unit().requireInside(opacity).number().doubleValue();
    return new GrayscaleColorData(toInt(value));
  }

  private static int toInt(double value) {
    return (int) (value * 255 + 0.5);
  }
}
