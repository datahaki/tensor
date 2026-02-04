// code by jph
package ch.alpine.tensor.jet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class AppendOneTest {
  @Test
  void testSimple() {
    Tensor vector = Tensors.vector(2, 3);
    Tensor result = AppendOne.FUNCTION.apply(vector);
    assertEquals(vector, Tensors.vector(2, 3));
    assertEquals(result, Tensors.vector(2, 3, 1));
  }

  @Test
  void testEmptyFail() {
    assertThrows(Exception.class, () -> AppendOne.FUNCTION.apply(Tensors.empty()));
  }
}
