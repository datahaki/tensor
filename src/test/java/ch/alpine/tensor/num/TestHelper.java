// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.Tensors;

/* package */ enum TestHelper {
  ;
  public static Cycles of(String string) {
    return Cycles.of(Tensors.fromString(string));
  }
}
