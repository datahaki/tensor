// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.LongBuffer;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Join;

class PrimitivesLongTest {
  @Test
  void testToListLong() {
    Tensor tensor = Tensors.vector(-2.5, -2.7, 4.3, 5.4, 6.2, 10.5);
    List<Long> list = Primitives.toListLong(tensor);
    assertEquals(Arrays.asList(-2L, -2L, 4L, 5L, 6L, 10L), list);
  }

  @Test
  void testToLongArray() {
    Tensor a = Tensors.vector(-2, -3, 4, 5, 6, 11, Long.MAX_VALUE);
    Tensor b = Tensors.vector(-2.5, -3.7, 4.3, 5.4, 6.2, 11.5, Long.MAX_VALUE);
    List<Long> listA = Primitives.toListLong(a);
    List<Long> listB = Primitives.toListLong(b);
    assertEquals(a, Tensors.vector(listA));
    assertEquals(a, Tensors.vector(listB));
    long[] array = Primitives.toLongArray(a);
    assertEquals(array[6], Long.MAX_VALUE);
  }

  @Test
  void testToLongBuffer() {
    Tensor a = Tensors.vector(-2, -27, Math.PI);
    Tensor b = Tensors.vector(43, 54, 62, 105);
    LongBuffer longBuffer = Primitives.toLongBuffer(Join.of(a, b));
    assertEquals(longBuffer.get(), -2);
    assertEquals(longBuffer.get(), -27);
    assertEquals(longBuffer.get(), 3);
    assertEquals(longBuffer.get(), 43);
    assertEquals(longBuffer.get(), 54);
    assertEquals(longBuffer.limit(), 7);
  }
}
