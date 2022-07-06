// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.nrm.Normalize;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.pow.Sqrt;

class StandardDeviationTest {
  @Test
  void testSimple() {
    Scalar scalar = StandardDeviation.ofVector(Tensors.vector(1, 2, 6, 3, -2, 3, 10));
    assertEquals(scalar, Sqrt.of(RationalScalar.of(102, 7)));
  }

  @Test
  void testNormalize() {
    TensorUnaryOperator tensorUnaryOperator = Normalize.with(StandardDeviation::ofVector);
    Tensor tensor = Tensors.vector(1, 5, 3, 7, 5, 2);
    Tensor result = tensorUnaryOperator.apply(tensor);
    Chop._14.requireClose(StandardDeviation.ofVector(result), RealScalar.ONE);
  }

  @Test
  void testScalarFail() {
    assertThrows(Throw.class, () -> StandardDeviation.ofVector(RealScalar.ONE));
  }

  @Test
  void testMatrixFail() {
    assertThrows(ClassCastException.class, () -> StandardDeviation.ofVector(HilbertMatrix.of(3)));
  }

  @Test
  void testDistribution() {
    Scalar scalar = StandardDeviation.of(UniformDistribution.of(10, 20));
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(2.8867513459481287));
  }
}
