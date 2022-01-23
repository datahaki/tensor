// code by jph
package ch.alpine.tensor.qty;

import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Clip;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/QuantityUnit.html">QuantityUnit</a> */
public enum QuantityUnit {
  ;
  /** @param scalar non-null
   * @return unit associated with the specified scalar
   * @throws Exception if given scalar is null */
  public static Unit of(Scalar scalar) {
    if (scalar instanceof Quantity quantity)
      return quantity.unit();
    Objects.requireNonNull(scalar);
    return Unit.ONE;
  }

  /** @param clip
   * @return shared unit of clip.min and clip.max */
  public static Unit of(Clip clip) {
    return of(clip.min());
  }
}
