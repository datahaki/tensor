// code by jph
package ch.alpine.tensor.img;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.Clips;

/* package */ class LookupColorData implements ColorDataGradient {
  public static final ColorDataGradient GRAYSCALE = new LookupColorData(createBW(255));

  private static Tensor[] createBW(int alpha) {
    return Stream.of(StaticHelper.LOOKUP) //
        .map(value -> Tensors.of(value, value, value, StaticHelper.LOOKUP[alpha])) //
        .toArray(Tensor[]::new);
  }

  // ---
  public static final ColorDataGradient GRAYSCALE_REVERSED = new LookupColorData(createWB(255));

  private static Tensor[] createWB(int alpha) {
    return IntStream.range(0, 256) //
        .mapToObj(i -> StaticHelper.LOOKUP[255 - i]) //
        .map(value -> Tensors.of(value, value, value, StaticHelper.LOOKUP[alpha])) //
        .toArray(Tensor[]::new);
  }

  // ---
  private final Tensor[] tensors;

  public LookupColorData(Tensor[] tensors) {
    this.tensors = tensors;
  }

  @Override // from ScalarTensorFunction
  public Tensor apply(Scalar scalar) {
    double value = scalar.number().doubleValue();
    return Double.isFinite(value) //
        ? tensors[toInt(value)].copy()
        : Transparent.rgba();
  }

  @Override // from ColorDataGradient
  public ColorDataGradient deriveWithOpacity(Scalar opacity) {
    double value = Clips.unit().requireInside(opacity).number().doubleValue(); // in [0, 1]
    Scalar alpha = RealScalar.of(toInt(value)); // in [0, 255]
    return new LookupColorData(Stream.of(tensors) //
        .map(rgba -> rgba.extract(0, 3).append(alpha)) //
        .toArray(Tensor[]::new));
  }

  private static int toInt(double value) {
    return (int) (value * 255 + 0.5);
  }
}
