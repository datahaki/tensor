// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.c.TrapezoidalDistribution;
import ch.alpine.tensor.qty.Quantity;

class CentralMomentTest {
  @Test
  void testVarious() {
    Tensor tensor = Tensors.vector(10, 2, 3, 4, 1);
    assertEquals(CentralMoment.of(tensor, 0), RealScalar.of(1));
    assertEquals(CentralMoment.of(tensor, 1), RealScalar.of(0));
    assertEquals(CentralMoment.of(tensor, 2), RealScalar.of(10));
    assertEquals(CentralMoment.of(tensor, 3), RealScalar.of(36));
    assertEquals(CentralMoment.of(tensor, 4), Scalars.fromString("1394/5"));
  }

  @Test
  void testTrapezoidal() {
    Distribution distribution = TrapezoidalDistribution.of(2, 3, 4, 7);
    Scalar scalar = CentralMoment.of(distribution, 3);
    ExactScalarQ.require(scalar);
    assertEquals(scalar, Rational.of(326, 729));
    assertEquals(CentralMoment.of(distribution, 2), Variance.of(distribution));
    assertThrows(IllegalArgumentException.class, () -> CentralMoment.of(distribution, -1));
  }

  @Test
  void testComplex() {
    Tensor tensor = Tensors.vector(10, 2, 3, 4, 1);
    Scalar result = CentralMoment.of(tensor, 1.3);
    Scalar gndtru = Scalars.fromString("1.1567572194352718 - 1.2351191805935866* I");
    Tolerance.CHOP.requireClose(result, gndtru);
  }

  @Test
  void testNegative() {
    Tensor tensor = Tensors.vector(-10, -2, 3, 4);
    Scalar result = CentralMoment.of(tensor, -3.3);
    Scalar gndtru = Scalars.fromString("-0.3766679353623411 + 0.5227888787805336* I");
    Tolerance.CHOP.requireClose(result, gndtru);
  }

  @Test
  void testQuantity() {
    Tensor vector = Tensors.of(Quantity.of(2, "kg"), Quantity.of(3, "kg"));
    Scalar result = CentralMoment.of(vector, 2);
    assertEquals(result, Scalars.fromString("1/4[kg^2]"));
  }

  @Test
  void testEmptyFail() {
    assertThrows(ArithmeticException.class, () -> CentralMoment.of(Tensors.empty(), 2));
  }

  @Test
  void testMatrixFail() {
    assertThrows(ClassCastException.class, () -> CentralMoment.of(HilbertMatrix.of(2, 3), RealScalar.of(2)));
  }
}
