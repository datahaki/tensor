// code by jph
package ch.alpine.tensor.img;

import java.awt.Color;
import java.util.Arrays;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** reference implementation of a {@link ColorDataIndexed} with strict indexing
 * 
 * color indices are required to be in the range 0, 1, ..., tensor.length() - 1 */
public class StrictColorDataIndexed extends BaseColorDataIndexed {
  /** Hint: tensor may be empty
   * 
   * @param tensor with dimensions N x 4 where each row encodes {R, G, B, A}
   * @return
   * @see CyclicColorDataIndexed */
  public static ColorDataIndexed of(Tensor tensor) {
    return new StrictColorDataIndexed(tensor.copy());
  }

  /** @param colors
   * @return palette of given colors where index maps to colors[index] */
  @SafeVarargs
  public static ColorDataIndexed of(Color... colors) {
    return new StrictColorDataIndexed(Tensor.of(Arrays.stream(colors).map(ColorFormat::toVector)));
  }

  // ---
  /** @param tensor with dimensions N x 4 where each row encodes {R, G, B, A} */
  /* package */ StrictColorDataIndexed(Tensor tensor) {
    super(tensor);
  }

  @Override // from ColorDataIndexed
  public Color getColor(int index) {
    return colors[index];
  }

  @Override // from ColorDataIndexed
  public ColorDataIndexed deriveWithAlpha(int alpha) {
    return new StrictColorDataIndexed(tableWithAlpha(alpha));
  }

  @Override // from BaseColorDataIndexed
  protected int toInt(Scalar scalar) {
    return scalar.number().intValue();
  }
}
