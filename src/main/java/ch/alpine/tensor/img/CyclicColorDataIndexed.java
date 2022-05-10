// code by jph
package ch.alpine.tensor.img;

import java.awt.Color;
import java.util.Arrays;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Mod;

/** reference implementation of a {@link ColorDataIndexed} with cyclic indexing */
public class CyclicColorDataIndexed extends BaseColorDataIndexed {
  /** @param tensor with dimensions N x 4 where each row encodes {R, G, B, A}
   * @return
   * @throws Exception if tensor is empty
   * @see StrictColorDataIndexed */
  public static ColorDataIndexed of(Tensor tensor) {
    return new CyclicColorDataIndexed(tensor.copy());
  }

  /** @param colors
   * @return palette of given colors where index maps to colors[index] */
  @SafeVarargs
  public static ColorDataIndexed of(Color... colors) {
    return new CyclicColorDataIndexed(Tensor.of(Arrays.stream(colors).map(ColorFormat::toVector)));
  }

  // ---
  private final Mod mod;

  /** @param tensor with dimensions N x 4 where each row encodes {R, G, B, A} */
  /* package */ CyclicColorDataIndexed(Tensor tensor) {
    super(tensor);
    mod = Mod.function(tensor.length());
  }

  @Override // from ColorDataIndexed
  public Color getColor(int index) {
    return colors[Math.floorMod(index, colors.length)];
  }

  @Override // from ColorDataIndexed
  public ColorDataIndexed deriveWithAlpha(int alpha) {
    return new CyclicColorDataIndexed(tableWithAlpha(alpha));
  }

  @Override // from BaseColorDataIndexed
  protected int toInt(Scalar scalar) {
    return mod.apply(scalar).number().intValue();
  }
}
