// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.qty.DateTime;

class ImTest {
  @Test
  void testExact() {
    Scalar scalar = Im.FUNCTION.apply(Scalars.fromString("3+I*6/7"));
    assertEquals(scalar, RationalScalar.of(6, 7));
    ExactScalarQ.require(scalar);
  }

  @Test
  void testTensorExact() {
    Tensor tensor = Im.of(Tensors.fromString("{{3+I*6/7, 5*I}, 2, {}}"));
    assertEquals(tensor, Tensors.fromString("{{6/7, 5}, 0, {}}"));
    ExactTensorQ.require(tensor);
  }

  @Test
  void testIncrement() {
    Tensor matrix = Tensors.matrixInt(new int[][] { { -8, 3, -3 }, { 2, -2, 7 } });
    matrix.set(RealScalar.ONE::add, 0, 0);
    Tensor result = matrix.map(RealScalar.ONE::add);
    Tensor check = Tensors.matrixInt(new int[][] { { -6, 4, -2 }, { 3, -1, 8 } });
    assertEquals(result, check);
  }

  @Test
  void testDecrement() {
    Tensor matrix = Tensors.matrixInt(new int[][] { { -8, 3, -3 }, { 2, -2, 7 } });
    matrix.set(s -> s.subtract(RealScalar.ONE), 0, 0);
    Tensor result = matrix.map(s -> s.subtract(RealScalar.ONE));
    Tensor check = Tensors.matrixInt(new int[][] { { -10, 2, -4 }, { 1, -3, 6 } });
    assertEquals(result, check);
  }

  @Test
  void testFail() {
    Scalar scalar = StringScalar.of("string");
    assertThrows(Throw.class, () -> Im.of(scalar));
  }

  @Test
  void testDateTimeFail() {
    Scalar scalar = DateTime.now();
    assertThrows(Throw.class, () -> Im.of(scalar));
  }
}
