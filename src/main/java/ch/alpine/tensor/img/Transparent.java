// code by gjoel and jph
package ch.alpine.tensor.img;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Transparent.html">Transparent</a> */
/* package */ enum Transparent {
  ;
  private static final Tensor RGBA = Tensors.vector(_ -> RealScalar.ZERO, 4);

  /** @return {0, 0, 0, 0} */
  public static Tensor rgba() {
    return RGBA.copy();
  }
}
