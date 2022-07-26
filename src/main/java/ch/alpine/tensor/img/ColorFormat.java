// code by jph
package ch.alpine.tensor.img;

import java.awt.Color;
import java.util.stream.IntStream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.io.ImageFormat;

/** mappings between {@link Tensor}, {@link Color}, and 0xAA:RR:GG:BB integer
 * 
 * <p>functions are used in {@link ImageFormat} */
public enum ColorFormat {
  ;
  /** there are only [0, 1, ..., 255] possible values for red, green, blue, and alpha.
   * We preallocate instances of these scalars in a lookup table to save memory and
   * possibly enhance execution time. */
  private static final Scalar[] LOOKUP = //
      IntStream.range(0, 256).mapToObj(RealScalar::of).toArray(Scalar[]::new);

  /** @param color
   * @return vector with {@link Scalar} entries as {R, G, B, A} */
  public static Tensor toVector(Color color) {
    return Tensors.of( //
        LOOKUP[color.getRed()], //
        LOOKUP[color.getGreen()], //
        LOOKUP[color.getBlue()], //
        LOOKUP[color.getAlpha()]);
  }

  /** @param argb encoding color as 0xAA:RR:GG:BB
   * @return vector with {@link Scalar} entries as {R, G, B, A} */
  public static Tensor toVector(int argb) {
    return toVector(new Color(argb, true));
  }

  /** @param vector with {@link Scalar} entries as {R, G, B, A}
   * @return encoding color as 0xAA:RR:GG:BB
   * @throws Exception if either color value is outside the allowed range [0, ..., 255] */
  public static Color toColor(Tensor vector) {
    if (vector.length() != 4)
      throw new Throw(vector);
    return new Color( //
        vector.Get(0).number().intValue(), //
        vector.Get(1).number().intValue(), //
        vector.Get(2).number().intValue(), //
        vector.Get(3).number().intValue());
  }

  /** @param vector with {@link Scalar} entries as {R, G, B, A}
   * @return int in hex 0xAA:RR:GG:BB */
  public static int toInt(Tensor vector) {
    return toColor(vector).getRGB();
  }
}
