// code by jph
package ch.alpine.tensor.nrm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.num.Pi;

class NormalizeTotalTest {
  @Test
  void testSimple() {
    Tensor tensor = NormalizeTotal.FUNCTION.apply(Tensors.vector(2, -3, 4, 5));
    assertEquals(tensor, Tensors.fromString("{1/4, -3/8, 1/2, 5/8}"));
  }

  @Test
  void testUnitVector() {
    Tensor tensor = NormalizeTotal.FUNCTION.apply(Tensors.vector(2, 0.0, 4, 5).maps(Scalar::reciprocal));
    assertEquals(tensor, UnitVector.of(4, 1));
  }

  @Test
  void testEmpty() {
    assertThrows(Throw.class, () -> NormalizeTotal.FUNCTION.apply(Tensors.empty()));
  }

  @Test
  void testZeroFail() {
    assertThrows(ArithmeticException.class, () -> NormalizeTotal.FUNCTION.apply(Tensors.vector(2, -2, 1, -1)));
  }

  @Test
  void testZeroNumericFail() {
    assertThrows(Throw.class, () -> NormalizeTotal.FUNCTION.apply(Tensors.vectorDouble(2, -2, 1, -1)));
  }

  @Test
  void testFailScalar() {
    assertThrows(Throw.class, () -> NormalizeTotal.FUNCTION.apply(Pi.TWO));
  }

  @Test
  void testFailMatrix() {
    assertThrows(ClassCastException.class, () -> NormalizeTotal.FUNCTION.apply(HilbertMatrix.of(3)));
  }
}
