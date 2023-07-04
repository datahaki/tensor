// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Flatten;

class PrimitivesTest {
  @Test
  void testByteArray() {
    Tensor tensor = Flatten.of(Tensors.fromString("{{1, 2, 3}, -1, {{256}}}"));
    byte[] array = Primitives.toByteArray(tensor);
    assertEquals(array[0], 1);
    assertEquals(array[1], 2);
    assertEquals(array[3], -1);
    assertEquals(array[4], 0);
    assertEquals(array.length, 5);
  }

  @Test
  void testByteBuffer() {
    Tensor tensor = Flatten.of(Tensors.fromString("{{1, 2, 3}, -1, {{256}}}"));
    ByteBuffer byteBuffer = Primitives.toByteBuffer(tensor);
    assertEquals(byteBuffer.get() & 0xff, 1);
    assertEquals(byteBuffer.get() & 0xff, 2);
    assertEquals(byteBuffer.get() & 0xff, 3);
    assertEquals(byteBuffer.get() & 0xff, 255);
    assertEquals(byteBuffer.get() & 0xff, 0);
  }
}
