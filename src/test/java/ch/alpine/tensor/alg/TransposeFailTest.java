// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;

class TransposeFailTest {
  @Test
  void testScalarFail() {
    Tensor v = DoubleScalar.NEGATIVE_INFINITY;
    assertThrows(TensorRuntimeException.class, () -> Transpose.of(v));
    assertThrows(IndexOutOfBoundsException.class, () -> Transpose.of(v, new int[] { 2 }));
  }

  @Test
  void testEmptyFail() {
    assertThrows(TensorRuntimeException.class, () -> Transpose.of(Tensors.empty()));
  }

  @Test
  void testVectorFail() {
    assertThrows(TensorRuntimeException.class, () -> Transpose.of(Tensors.vector(2, 3, 4, 5)));
  }

  @Test
  void testEmpty2() {
    Tensor empty2 = Tensors.fromString("{{}, {}}");
    assertEquals(Transpose.of(empty2), Tensors.empty());
    assertThrows(TensorRuntimeException.class, () -> Transpose.of(Transpose.of(empty2)));
  }

  @Test
  void testRankFail() {
    Transpose.of(Array.zeros(1, 3, 2), 1, 2, 0);
    Transpose.of(Array.zeros(3, 3, 3), 1, 0);
    assertThrows(IndexOutOfBoundsException.class, () -> Transpose.of(Array.zeros(3, 3, 2), 3, 2, 1, 0));
  }

  @Test
  void testFail2() {
    assertThrows(TensorRuntimeException.class, () -> Transpose.of(Tensors.fromString("{{1, 2}, {3, 4, 5}}")));
    assertThrows(TensorRuntimeException.class, () -> Transpose.of(Tensors.fromString("{{1, 2, 3}, {4, 5}}")));
  }

  @Test
  void testNonPermutationFail() {
    Tensor matrix = Array.zeros(2, 3);
    assertThrows(IllegalArgumentException.class, () -> Transpose.of(matrix, 1));
    assertThrows(IllegalArgumentException.class, () -> Transpose.of(matrix, 2, 0));
    assertThrows(IllegalArgumentException.class, () -> Transpose.of(matrix, 0, -1));
  }

  @Test
  void testNonPermFail1() {
    Tensor matrix = Array.zeros(2, 3);
    assertThrows(IllegalArgumentException.class, () -> Transpose.of(matrix, 0, 0));
    assertThrows(IllegalArgumentException.class, () -> Transpose.of(matrix, 1, 1));
  }

  @Test
  void testNonPermFail2() {
    Tensor matrix = Array.zeros(3, 2);
    assertThrows(IllegalArgumentException.class, () -> Transpose.of(matrix, 0, 0));
    assertThrows(IllegalArgumentException.class, () -> Transpose.of(matrix, 1, 1));
  }

  @Test
  void testNonPermFail3() {
    Tensor matrix = Array.zeros(3, 2, 1);
    assertThrows(IllegalArgumentException.class, () -> Transpose.of(matrix, 0, 1, 0));
    assertThrows(IllegalArgumentException.class, () -> Transpose.of(matrix, 1, 0, 1));
    assertThrows(IllegalArgumentException.class, () -> Transpose.of(matrix, 0, 1, 1));
    assertThrows(IllegalArgumentException.class, () -> Transpose.of(matrix, 2, 2, 1));
  }
}
