// code by jph
package ch.alpine.tensor.red;

import java.util.Objects;
import java.util.OptionalInt;

import ch.alpine.tensor.Tensor;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/FirstPosition.html">FirstPosition</a> */
public enum FirstPosition {
  ;
  /** @param tensor non-null
   * @param element non-null
   * @return smallest index with tensor.get(index).equals(element) or OptionalInt.empty() */
  public static OptionalInt of(Tensor tensor, Tensor element) {
    int index = 0;
    for (Tensor row : tensor) {
      if (element.equals(row))
        return OptionalInt.of(index);
      ++index;
    }
    Objects.requireNonNull(element);
    return OptionalInt.empty();
  }
}
