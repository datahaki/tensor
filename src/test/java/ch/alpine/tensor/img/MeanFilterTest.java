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

class MeanFilterTest {
  @Test
  public void testId() {
    Tensor vector1 = Tensors.vector(1, 2, 3, 4, 5, 6);
    Tensor result1 = MeanFilter.of(vector1, 0);
    assertEquals(vector1, result1);
  }

  @Test
  public void testMean1() {
    Tensor vector1 = Tensors.vector(1, 4, 4, 1);
    Tensor result1 = MeanFilter.of(vector1, 1);
    assertEquals(Tensors.vector(2.5, 3, 3, 2.5), result1);
  }

  @Test
  public void testMean2() {
    Tensor vector2 = Tensors.vector(5, 10, 15, 20, 25, 30, 40, 45, 50);
    Tensor result2 = MeanFilter.of(vector2, 2);
    assertEquals(Tensors.vector(10, 12.5, 15, 20, 26, 32, 38, 41.25, 45), result2);
  }

  @Test
  public void testMean3() {
    Tensor vector1 = Tensors.vector(-3, 3, 6, 0, 0, 3, -3, -9);
    Tensor result1 = MeanFilter.of(vector1, 1);
    assertEquals(Tensors.vector(0, 2, 3, 2, 1, 0, -3, -6), result1);
  }

  @Test
  public void testEmpty() {
    Tensor input = Tensors.empty();
    Tensor result = MeanFilter.of(input, 2);
    input.append(RealScalar.ZERO);
    assertEquals(result, Tensors.empty());
  }

  @Test
  public void testMatrix() {
    Tensor matrix = Tensors.fromString("{{1, 2, 3, 2, 1, 0, 0, 1, 0}, {3, 3, 3, 2, 2, 2, 1, 1, 1}, {0, 0, 0, 0, 0, 0, 0, 0, 0}}");
    assertEquals(MeanFilter.of(matrix, 0), matrix);
    Tensor result = MeanFilter.of(matrix, 1);
    String mathematica = //
        "{{9/4, 5/2, 5/2, 13/6, 3/2, 1, 5/6, 2/3, 3/4}, {3/2, 5/3, 5/3, 13/9, 1, 2/3, 5/9, 4/9, 1/2}, {3/2, 3/2, 4/3, 7/6, 1, 5/6, 2/3, 1/2, 1/2}}";
    assertEquals(result, Tensors.fromString(mathematica));
  }

  @Test
  public void testScalarFail() {
    assertThrows(TensorRuntimeException.class, () -> MeanFilter.of(RealScalar.of(3), 1));
  }

  @Test
  public void testNonArrayFail() {
    Tensor matrix = Tensors.fromString("{{1, 2, 3, 3, {3, 2, 3}}, {3}, {0, 0, 0}}");
    matrix.flatten(-1).forEach(RationalScalar.class::cast); // test if parsing went ok
    MeanFilter.of(matrix, 0);
    assertThrows(IllegalArgumentException.class, () -> MeanFilter.of(matrix, 1));
  }

  @Test
  public void testRadiusFail() {
    assertThrows(IllegalArgumentException.class, () -> MeanFilter.of(Tensors.vector(1, 2, 3, 4), -1));
  }
}
