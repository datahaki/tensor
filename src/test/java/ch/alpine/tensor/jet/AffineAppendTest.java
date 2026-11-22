// code by jph
package ch.alpine.tensor.jet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class AffineAppendTest {
  @Test
  void testSimple() {
    Tensor vector = Tensors.vector(2, 3);
    Tensor result = AffineAppend.M1.apply(vector);
    assertEquals(vector, Tensors.vector(2, 3));
    assertEquals(result, Tensors.vector(2, 3, 1));
  }

  @Test
  void testEmptyFail() {
    assertThrows(Exception.class, () -> AffineAppend.M1.apply(Tensors.empty()));
  }

  @Test
  void testBlub() {
    Tensor v = Tensors.fromString("{2[m],1}");
    Tensor m = Tensors.fromString("{{1[m^-1],1},{1[m^-1],1}}");
    m.dot(v);
  }
}
