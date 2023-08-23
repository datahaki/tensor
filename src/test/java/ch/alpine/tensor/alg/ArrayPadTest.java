// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.qty.Quantity;

class ArrayPadTest {
  @Test
  void testVector() {
    Tensor vec = Tensors.vector(2, 3, -3, 1);
    Tensor pad = ArrayPad.of(vec, List.of(3), List.of(4));
    Tensor actual = Tensors.vector(0, 0, 0, 2, 3, -3, 1, 0, 0, 0, 0);
    assertEquals(pad, actual);
  }

  @Test
  void testVectorQantity() {
    Tensor vec = Tensors.fromString("{0[m],0.0[m]}");
    Tensor res = ArrayPad.of(vec, List.of(1), List.of(1));
    assertEquals(res.toString(), "{0.0[m], 0[m], 0.0[m], 0.0[m]}");
  }

  @Test
  void testVectorQantityFail() {
    Tensor vec = Tensors.fromString("{0[m], 2[s]}");
    assertThrows(Exception.class, () -> ArrayPad.of(vec, List.of(1), List.of(1)));
  }

  @Test
  void testMatrix() {
    Tensor matrix = Tensors.of(Tensors.vector(2, 3, 1), Tensors.vector(7, 8, 9));
    assertEquals(Dimensions.of(matrix), Arrays.asList(2, 3));
    Tensor pad = ArrayPad.of(matrix, Arrays.asList(1, 2), Arrays.asList(3, 4));
    assertEquals(Dimensions.of(pad), Arrays.asList(1 + 2 + 3, 2 + 3 + 4));
  }

  @Test
  void testMatrixQuantity() {
    Tensor tensor = HilbertMatrix.of(2, 3).map(s -> Quantity.of(s, "K"));
    Tensor result = ArrayPad.of(tensor, List.of(3, 4), List.of(5, 6));
    List<Integer> list = Dimensions.of(result);
    assertEquals(list, List.of(10, 13));
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
    Tensor vector = ArrayPad.of(tensor, List.of(2), List.of(3));
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
    assertThrows(IllegalArgumentException.class, () -> ArrayPad.of(vector, List.of(1), List.of(-2)));
    assertThrows(IllegalArgumentException.class, () -> ArrayPad.of(vector, List.of(-1), List.of(2)));
  }
}
