// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;

class DropTest {
  @Test
  void testHead() {
    assertEquals(Drop.head(Tensors.empty(), 0), Tensors.empty());
    assertEquals(Drop.head(Tensors.vector(9, 8, 3), 1), Tensors.vector(8, 3));
  }

  @Test
  void testMatrix() {
    assertEquals(Drop.tail(HilbertMatrix.of(10, 10), 2), HilbertMatrix.of(8, 10));
    assertEquals(Drop.head(HilbertMatrix.of(10, 10), 2), HilbertMatrix.of(10, 10).extract(2, 10));
  }

  @Test
  void testHeadFail() {
    Drop.head(Tensors.vector(1, 2), 0);
    Drop.head(Tensors.vector(1, 2), 1);
    Drop.head(Tensors.vector(1, 2), 2);
    assertThrows(IllegalArgumentException.class, () -> Drop.head(Tensors.vector(1, 2), 3));
    assertThrows(IndexOutOfBoundsException.class, () -> Drop.head(Tensors.vector(1, 2), -1));
  }

  @Test
  void testTailFail() {
    Drop.tail(Tensors.vector(1, 2), 0);
    Drop.tail(Tensors.vector(1, 2), 1);
    Drop.tail(Tensors.vector(1, 2), 2);
    assertThrows(IllegalArgumentException.class, () -> Drop.tail(Tensors.vector(1, 2), 3));
    assertThrows(IndexOutOfBoundsException.class, () -> Drop.tail(Tensors.vector(1, 2), -1));
  }
}
