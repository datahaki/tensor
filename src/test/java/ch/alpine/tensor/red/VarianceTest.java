// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.TensorMap;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.pdf.c.UniformDistribution;

class VarianceTest {
  @Test
  void testVariance() {
    Tensor A = Tensors.vector(1, 2, 5, 7);
    assertEquals(Mean.of(A), RationalScalar.of(15, 4));
    assertEquals(Variance.ofVector(A), RationalScalar.of(91, 12));
  }

  @Test
  void testVariance2() {
    Tensor A = Tensors.of( //
        Tensors.vector(1, 2, 5, 7), //
        Tensors.vector(1, 2, 5) //
    );
    Tensor b = TensorMap.of(Variance::ofVector, A, 1);
    Tensor c = Tensors.fromString("{91/12, 13/3}");
    assertEquals(b, c);
  }

  @Test
  void testComplex() {
    Tensor vector = Tensors.of(ComplexScalar.of(1, 7), ComplexScalar.of(2, -3), ComplexScalar.of(3, 2));
    Tensor v = Variance.ofVector(vector);
    assertEquals(v, RealScalar.of(26));
  }

  @Test
  void testDistribution() {
    assertEquals(Variance.of(UniformDistribution.unit()), RationalScalar.of(1, 12));
  }

  @Test
  void testFailScalar() {
    assertThrows(Throw.class, () -> Variance.ofVector(RealScalar.ONE));
  }

  @Test
  void testFailLength() {
    assertThrows(ArithmeticException.class, () -> Variance.ofVector(Tensors.empty()));
    assertThrows(ArithmeticException.class, () -> Variance.ofVector(Tensors.vector(3)));
  }

  @Test
  void testFailMatrix() {
    assertThrows(ClassCastException.class, () -> Variance.ofVector(HilbertMatrix.of(5)));
  }
}
