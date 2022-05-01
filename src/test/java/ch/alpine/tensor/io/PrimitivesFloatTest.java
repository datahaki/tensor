// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.FloatBuffer;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class PrimitivesFloatTest {
  @Test
  public void testToListFloat() {
    Tensor tensor = Tensors.vector(-2.5f, -2.7f);
    List<Float> list = Primitives.toListFloat(tensor);
    assertEquals(list.get(0), -2.5f);
    assertEquals(list.get(1), -2.7f);
    assertEquals(list.size(), 2);
  }

  @Test
  public void testToFloatArray() {
    Tensor a = Tensors.vector(-2.5f, -2.7f);
    Tensor b = Tensors.vector(4.3f, 5.4f, 6.2f, 10.5f);
    float[] array = Primitives.toFloatArray(Tensors.of(a, b));
    assertEquals(array[0], -2.5f);
    assertEquals(array[1], -2.7f);
    assertEquals(array[2], 4.3f);
    assertEquals(array[3], 5.4f);
    assertEquals(array[4], 6.2f);
    assertEquals(array[5], 10.5f);
    assertEquals(array.length, 6);
  }

  @Test
  public void testToFloatArray2D() {
    Tensor a = Tensors.vector(-2.5f, -2.7f);
    Tensor b = Tensors.vector(4.3f, 5.4f, 6.2f, 10.5f);
    float[][] array = Primitives.toFloatArray2D(Tensors.of(a, b));
    assertEquals(array[0][0], -2.5f);
    assertEquals(array[0][1], -2.7f);
    assertEquals(array[1][0], 4.3f);
    assertEquals(array[1][1], 5.4f);
    assertEquals(array[1][2], 6.2f);
    assertEquals(array[1][3], 10.5f);
    assertEquals(array.length, 2);
    assertEquals(array[0].length, 2);
    assertEquals(array[1].length, 4);
  }

  @Test
  public void testToFloatBuffer() {
    Tensor a = Tensors.vector(-2.5f, -2.7f);
    Tensor b = Tensors.vector(4.3f, 5.4f, 6.2f, 10.5f);
    FloatBuffer floatBuffer = Primitives.toFloatBuffer(Tensors.of(a, b));
    assertEquals(floatBuffer.get(), -2.5f);
    assertEquals(floatBuffer.get(), -2.7f);
    assertEquals(floatBuffer.get(), 4.3f);
    assertEquals(floatBuffer.get(), 5.4f);
    assertEquals(floatBuffer.limit(), 6);
  }

  @Test
  public void testToFloatArray2Dscalar() {
    assertThrows(NegativeArraySizeException.class, () -> Primitives.toFloatArray2D(RealScalar.of(123.456)));
  }
}
