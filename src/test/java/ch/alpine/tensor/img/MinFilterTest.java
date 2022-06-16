// code by gjoel and jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactTensorQ;

class MinFilterTest {
  @Test
  void testId() {
    Tensor vector = Tensors.vector(1, 2, 3, 4, 5, 6);
    Tensor result = MinFilter.of(vector, 0);
    assertEquals(vector, result);
  }

  @Test
  void testMean1() {
    Tensor vector = Tensors.vector(1, 4, 4, 1);
    Tensor result = MinFilter.of(vector, 1);
    assertEquals(Tensors.vector(1, 1, 1, 1), result);
  }

  @Test
  void testMean2() {
    Tensor vector = Tensors.vector(5, 10, 15, 20, 25, 30, 40, 45, 50);
    Tensor result = MinFilter.of(vector, 2);
    ExactTensorQ.require(result);
    assertEquals(Tensors.vector(5, 5, 5, 10, 15, 20, 25, 30, 40), result);
  }

  @Test
  void testMean3() {
    Tensor vector = Tensors.vector(-3, 3, 6, 0, 0, 3, -3, -9);
    Tensor result = MinFilter.of(vector, 1);
    assertEquals(Tensors.vector(-3, -3, 0, 0, 0, -3, -9, -9), result);
  }

  @Test
  void testEmpty() {
    Tensor input = Tensors.empty();
    Tensor result = MinFilter.of(input, 2);
    input.append(RealScalar.ZERO);
    assertEquals(result, Tensors.empty());
  }

  @Test
  void testMatrix() {
    Tensor matrix = Tensors.fromString("{{1, 2, 3, 2, 1, 0, 0, 1, 0}, {3, 3, 3, 2, 2, 2, 1, 1, 1}, {0, 0, 0, 0, 0, 0, 0, 0, 0}}");
    assertEquals(MinFilter.of(matrix, 0), matrix);
    Tensor result = MinFilter.of(matrix, 1);
    String mathematica = "{{1, 1, 2, 1, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0}}";
    assertEquals(result, Tensors.fromString(mathematica));
  }

  @Test
  void testScalarFail() {
    assertThrows(TensorRuntimeException.class, () -> MinFilter.of(RealScalar.of(3), 1));
  }

  @Test
  void testNonArray() {
    Tensor matrix = Tensors.fromString("{{1, 2, 3, 3, {3, 2, 3}}, {3}, {0, 0, 0}}");
    matrix.flatten(-1).forEach(RationalScalar.class::cast); // test if parsing went ok
    MinFilter.of(matrix, 0);
    assertThrows(IllegalArgumentException.class, () -> MinFilter.of(matrix, 1));
  }

  @Test
  void testRadiusFail() {
    assertThrows(IllegalArgumentException.class, () -> MinFilter.of(Tensors.vector(1, 2, 3, 4), -1));
  }
}
