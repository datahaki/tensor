// code by jph
package ch.alpine.tensor.img;

import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/* package */ enum StaticHelper {
  ;
  /** @param type
   * @return either byte_gray or int_argb */
  public static int type(int type) {
    return type == BufferedImage.TYPE_BYTE_GRAY //
        ? BufferedImage.TYPE_BYTE_GRAY
        : BufferedImage.TYPE_INT_ARGB;
  }

  // ---
  public static final ColorDataGradient GRAYSCALE = new LookupColorData(createBW(255));

  private static Tensor[] createBW(int alpha) {
    Scalar a_ref = RealScalar.of(alpha);
    return IntStream.range(0, 256) //
        .mapToObj(RealScalar::of) //
        .map(value -> Tensors.of(value, value, value, a_ref)) //
        .toArray(Tensor[]::new);
  }

  // ---
  public static final ColorDataGradient GRAYSCALE_REVERSED = new LookupColorData(createWB(255));

  private static Tensor[] createWB(int alpha) {
    Scalar a_ref = RealScalar.of(alpha);
    return IntStream.range(0, 256) //
        .map(i -> 255 - i) //
        .mapToObj(RealScalar::of) //
        .map(value -> Tensors.of(value, value, value, a_ref)) //
        .toArray(Tensor[]::new);
  }
}
