// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;

class ArrayPadTest {
  @Test
  void testVector() {
    Tensor vec = Tensors.vector(2, 3, -3, 1);
    Tensor pad = ArrayPad.of(vec, Arrays.asList(3), Arrays.asList(4));
    Tensor actual = Tensors.vector(0, 0, 0, 2, 3, -3, 1, 0, 0, 0, 0);
    assertEquals(pad, actual);
  }

  @Test
  void testMatrix() {
    Tensor matrix = Tensors.of(Tensors.vector(2, 3, 1), Tensors.vector(7, 8, 9));
    assertEquals(Dimensions.of(matrix), Arrays.asList(2, 3));
    Tensor pad = ArrayPad.of(matrix, Arrays.asList(1, 2), Arrays.asList(3, 4));
    assertEquals(Dimensions.of(pad), Arrays.asList(1 + 2 + 3, 2 + 3 + 4));
  }

  @Test
  void testForm() {
    Tensor matrix = Tensors.of(Tensors.vector(2, 3, 1), Tensors.vector(7, 8, 9));
    Tensor form = Tensors.of(matrix, matrix, matrix, matrix);
    assertEquals(Dimensions.of(form), Arrays.asList(4, 2, 3));
    Tensor pad = ArrayPad.of(form, Arrays.asList(2, 1, 2), Arrays.asList(1, 3, 4));
    assertEquals(Dimensions.of(pad), Arrays.asList(2 + 4 + 1, 1 + 2 + 3, 2 + 3 + 4));
  }

  @Test
  void testNonArray() {
    Tensor tensor = Tensors.fromString("{{1, 2}, {3}}");
    Tensor vector = ArrayPad.of(tensor, Arrays.asList(2), Arrays.asList(3));
    assertEquals(vector.length(), 2 + 2 + 3);
  }

  @Test
  void testArrayAlternative() {
    Tensor tensor = Array.zeros(8, 8);
    tensor.block(Arrays.asList(2, 2), Arrays.asList(4, 4)).set(HilbertMatrix.of(4), Tensor.ALL, Tensor.ALL);
    assertEquals(Dimensions.of(tensor), Arrays.asList(8, 8));
  }

  @Test
  void testFail() {
    Tensor vector = Tensors.vector(2, 3, -3, 1);
    assertThrows(IllegalArgumentException.class, () -> ArrayPad.of(vector, Arrays.asList(1), Arrays.asList(-2)));
    assertThrows(IllegalArgumentException.class, () -> ArrayPad.of(vector, Arrays.asList(-1), Arrays.asList(2)));
  }
}
