// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class PrimitivesIntTest {
  @Test
  void testToListInteger() {
    Tensor tensor = Tensors.vector(-2.5, -2.7, 4.3, 5.4, 6.2, 10.5);
    List<Integer> list = Primitives.toListInteger(tensor);
    assertEquals(Arrays.asList(-2, -2, 4, 5, 6, 10), list);
  }

  @Test
  void testToListInteger2() {
    Tensor a = Tensors.vector(-2, -3, 4, 5, 6, 11);
    Tensor b = Tensors.vector(-2.5, -3.7, 4.3, 5.4, 6.2, 11.5);
    List<Integer> listA = Primitives.toListInteger(a);
    List<Integer> listB = Primitives.toListInteger(b);
    assertEquals(a, Tensors.vector(listA));
    assertEquals(a, Tensors.vector(listB));
  }

  @Test
  void testToIntArray() {
    Tensor a = Tensors.vector(-2, -3, 4, 5, 6, 11);
    Tensor b = Tensors.vector(-2.5, -3.7, 4.3, 5.4, 6.2, 11.5);
    assertArrayEquals(Primitives.toIntArray(a), //
        new int[] { -2, -3, 4, 5, 6, 11 });
    assertArrayEquals(Primitives.toIntArray(b), //
        new int[] { -2, -3, 4, 5, 6, 11 });
  }

  @Test
  void testToIntArray2D() {
    Tensor tensor = Tensors.fromString("{{1, 2}, {3, {4}, 5}, {6}}");
    int[][] array = Primitives.toIntArray2D(tensor);
    assertEquals(Tensors.vectorInt(array[0]), Tensors.vector(1, 2));
    assertEquals(Tensors.vectorInt(array[1]), Tensors.vector(3, 4, 5));
    assertEquals(Tensors.vectorInt(array[2]), Tensors.vector(6));
    assertEquals(array.length, 3);
  }

  @Test
  void testToIntArray2Dvector() {
    Tensor tensor = Tensors.fromString("{1, 2, {3, {4}, 5}, {{6}, 7}}");
    int[][] array = Primitives.toIntArray2D(tensor);
    assertEquals(Tensors.vectorInt(array[0]), Tensors.vector(1));
    assertEquals(Tensors.vectorInt(array[1]), Tensors.vector(2));
    assertEquals(Tensors.vectorInt(array[2]), Tensors.vector(3, 4, 5));
    assertEquals(Tensors.vectorInt(array[3]), Tensors.vector(6, 7));
    assertEquals(array.length, 4);
  }

  @Test
  void testToIntBuffer() {
    Tensor a = Tensors.vector(-2, -27, Math.PI);
    Tensor b = Tensors.vector(43, 54, 62, 105);
    IntBuffer intBuffer = Primitives.toIntBuffer(Tensors.of(a, b));
    assertEquals(intBuffer.get(), -2);
    assertEquals(intBuffer.get(), -27);
    assertEquals(intBuffer.get(), 3);
    assertEquals(intBuffer.get(), 43);
    assertEquals(intBuffer.get(), 54);
    assertEquals(intBuffer.limit(), 7);
  }

  @Test
  void testToIntArray2Dscalar() {
    assertThrows(NegativeArraySizeException.class, () -> Primitives.toIntArray2D(RealScalar.of(123.456)));
  }
}
