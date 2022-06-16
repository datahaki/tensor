// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensors;

class TensorExtractTest {
  @Test
  void testEmpty() {
    assertEquals(TensorExtract.of(Tensors.empty(), 0, t -> t), Tensors.empty());
  }

  @Test
  void testRadiusFail() {
    assertThrows(IllegalArgumentException.class, () -> TensorExtract.of(Tensors.empty(), -1, t -> t));
  }

  @Test
  void testFunctionNullFail() {
    assertThrows(NullPointerException.class, () -> TensorExtract.of(Tensors.empty(), 2, null));
  }
}
