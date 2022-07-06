// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.mat.HilbertMatrix;

class HarmonicMeanTest {
  @Test
  void testGeo1() {
    Tensor a = HarmonicMean.ofVector(Tensors.vector(8, 27, 525));
    assertEquals(a, RationalScalar.of(113400, 6197));
    Tensor b = HarmonicMean.ofVector(Tensors.vector(8, -27, 3));
    assertEquals(b, RationalScalar.of(648, 91));
  }

  @Test
  void testEmpty() {
    assertThrows(ArithmeticException.class, () -> HarmonicMean.ofVector(Tensors.empty()));
  }

  @Test
  void testZero() {
    assertThrows(ArithmeticException.class, () -> HarmonicMean.ofVector(Tensors.vector(3, 0, 2)));
  }

  @Test
  void testScalarFail() {
    assertThrows(Throw.class, () -> HarmonicMean.ofVector(RealScalar.ONE));
  }

  @Test
  void testMatrixFail() {
    assertThrows(ClassCastException.class, () -> HarmonicMean.ofVector(HilbertMatrix.of(4)));
  }
}
