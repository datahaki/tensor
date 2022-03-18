// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.StringScalar;

public class ImagTest {
  @Test
  public void testExact() {
    Scalar scalar = Imag.FUNCTION.apply(Scalars.fromString("3+I*6/7"));
    assertEquals(scalar, RationalScalar.of(6, 7));
    ExactScalarQ.require(scalar);
  }

  @Test
  public void testTensorExact() {
    Tensor tensor = Imag.of(Tensors.fromString("{{3+I*6/7, 5*I}, 2, {}}"));
    assertEquals(tensor, Tensors.fromString("{{6/7, 5}, 0, {}}"));
    ExactTensorQ.require(tensor);
  }

  @Test
  public void testIncrement() {
    Tensor matrix = Tensors.matrixInt(new int[][] { { -8, 3, -3 }, { 2, -2, 7 } });
    matrix.set(RealScalar.ONE::add, 0, 0);
    Tensor result = matrix.map(RealScalar.ONE::add);
    Tensor check = Tensors.matrixInt(new int[][] { { -6, 4, -2 }, { 3, -1, 8 } });
    assertEquals(result, check);
  }

  @Test
  public void testDecrement() {
    Tensor matrix = Tensors.matrixInt(new int[][] { { -8, 3, -3 }, { 2, -2, 7 } });
    matrix.set(s -> s.subtract(RealScalar.ONE), 0, 0);
    Tensor result = matrix.map(s -> s.subtract(RealScalar.ONE));
    Tensor check = Tensors.matrixInt(new int[][] { { -10, 2, -4 }, { 1, -3, 6 } });
    assertEquals(result, check);
  }

  @Test
  public void testFail() {
    Scalar scalar = StringScalar.of("string");
    assertThrows(TensorRuntimeException.class, () -> Imag.of(scalar));
  }
}
