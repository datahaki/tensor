// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.sca.Abs;

class TensorStreamTest {
  @Test
  void testStream() {
    Tensor row = IdentityMatrix.of(5).stream().skip(2).findFirst().orElseThrow();
    assertEquals(row, UnitVector.of(5, 2));
  }

  @Test
  void testReduction() {
    Tensor a = Tensors.vectorDouble(2., 1.123, 0.3123);
    assertTrue(Flatten.scalars(a) //
        .map(Scalar::number) //
        .map(Number::doubleValue) //
        .allMatch(d -> d > 0));
  }

  @Test
  void testNorm3() {
    Tensor a = Tensors.vectorLong(2, -3, 4, -1);
    double ods = Flatten.stream(a, 0) //
        .map(Scalar.class::cast) //
        .map(Abs.FUNCTION) //
        .map(Scalar::number) //
        .mapToDouble(Number::doubleValue) //
        .max() //
        .getAsDouble();
    assertEquals(ods, 4.0);
  }

  @Test
  void testNorm4() {
    Tensor a = Tensors.vectorLong(2, -3, 4, -1);
    double ods = Flatten.stream(a, 0) //
        .map(s -> (Scalar) s) //
        .map(Abs.FUNCTION) //
        .map(Scalar::number) //
        .mapToDouble(Number::doubleValue) //
        .sum();
    assertEquals(ods, 10.0);
  }
}
