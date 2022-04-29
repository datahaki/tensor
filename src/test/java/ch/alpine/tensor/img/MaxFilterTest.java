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

public class MaxFilterTest {
  @Test
  public void testId() {
    Tensor vector = Tensors.vector(1, 2, 3, 4, 5, 6);
    Tensor result = MaxFilter.of(vector, 0);
    assertEquals(vector, result);
  }

  @Test
  public void testMean1() {
    Tensor vector = Tensors.vector(1, 4, 4, 1);
    Tensor result = MaxFilter.of(vector, 1);
    assertEquals(Tensors.vector(4, 4, 4, 4), result);
  }

  @Test
  public void testMean2() {
    Tensor vector = Tensors.vector(5, 10, 15, 20, 25, 30, 40, 45, 50);
    Tensor result = MaxFilter.of(vector, 2);
    ExactTensorQ.require(result);
    assertEquals(Tensors.vector(15, 20, 25, 30, 40, 45, 50, 50, 50), result);
  }

  @Test
  public void testMean3() {
    Tensor vector = Tensors.vector(-3, 3, 6, 0, 0, 3, -3, -9);
    Tensor result = MaxFilter.of(vector, 1);
    assertEquals(Tensors.vector(3, 6, 6, 6, 3, 3, 3, -3), result);
  }

  @Test
  public void testEmpty() {
    Tensor input = Tensors.empty();
    Tensor result = MaxFilter.of(input, 2);
    input.append(RealScalar.ZERO);
    assertEquals(result, Tensors.empty());
  }

  @Test
  public void testMatrix() {
    Tensor matrix = Tensors.fromString("{{1, 2, 3, 2, 1, 0, 0, 1, 0}, {3, 3, 3, 2, 2, 2, 1, 1, 1}, {0, 0, 0, 0, 0, 0, 0, 0, 0}}");
    assertEquals(MaxFilter.of(matrix, 0), matrix);
    Tensor result = MaxFilter.of(matrix, 1);
    ExactTensorQ.require(result);
    String mathematica = "{{3, 3, 3, 3, 2, 2, 2, 1, 1}, {3, 3, 3, 3, 2, 2, 2, 1, 1}, {3, 3, 3, 3, 2, 2, 2, 1, 1}}";
    assertEquals(result, Tensors.fromString(mathematica));
  }

  @Test
  public void testScalarFail() {
    assertThrows(TensorRuntimeException.class, () -> MaxFilter.of(RealScalar.of(3), 1));
  }

  @Test
  public void testNonArray() {
    Tensor matrix = Tensors.fromString("{{1, 2, 3, 3, {3, 2, 3}}, {3}, {0, 0, 0}}");
    matrix.flatten(-1).forEach(RationalScalar.class::cast); // test if parsing went ok
    assertThrows(IllegalArgumentException.class, () -> MaxFilter.of(matrix, 1));
  }

  @Test
  public void testRadiusFail() {
    assertThrows(IllegalArgumentException.class, () -> MaxFilter.of(Tensors.vector(1, 2, 3, 4), -1));
  }
}
