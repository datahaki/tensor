// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.DateTime;

class ImTest {
  @Test
  void testExact() {
    Scalar scalar = Im.FUNCTION.apply(Scalars.fromString("3+I*6/7"));
    assertEquals(scalar, Rational.of(6, 7));
    ExactScalarQ.require(scalar);
    assertTrue(Im.allZero(Pi.VALUE));
  }

  @Test
  void testTensorExact() {
    Tensor tensor = Tensors.fromString("{{3+I*6/7, 5*I}, 2, {}}").maps(Im.FUNCTION);
    assertEquals(tensor, Tensors.fromString("{{6/7, 5}, 0, {}}"));
    ExactTensorQ.require(tensor);
  }

  @Test
  void testIncrement() {
    Tensor matrix = Tensors.matrixInt(new int[][] { { -8, 3, -3 }, { 2, -2, 7 } });
    matrix.set(RealScalar.ONE::add, 0, 0);
    Tensor result = matrix.maps(RealScalar.ONE::add);
    Tensor check = Tensors.matrixInt(new int[][] { { -6, 4, -2 }, { 3, -1, 8 } });
    assertEquals(result, check);
  }

  @Test
  void testDecrement() {
    Tensor matrix = Tensors.matrixInt(new int[][] { { -8, 3, -3 }, { 2, -2, 7 } });
    matrix.set(s -> s.subtract(RealScalar.ONE), 0, 0);
    Tensor result = matrix.maps(s -> s.subtract(RealScalar.ONE));
    Tensor check = Tensors.matrixInt(new int[][] { { -10, 2, -4 }, { 1, -3, 6 } });
    assertEquals(result, check);
  }

  @Test
  void testFail() {
    Scalar scalar = StringScalar.of("string");
    assertThrows(Throw.class, () -> Im.FUNCTION.apply(scalar));
  }

  @Test
  void testDateTimeFail() {
    Scalar scalar = DateTime.now();
    assertThrows(Throw.class, () -> Im.FUNCTION.apply(scalar));
  }
}
