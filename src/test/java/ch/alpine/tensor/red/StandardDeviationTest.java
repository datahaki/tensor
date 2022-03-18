// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.nrm.Normalize;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.usr.AssertFail;

public class StandardDeviationTest {
  @Test
  public void testSimple() {
    Scalar scalar = StandardDeviation.ofVector(Tensors.vector(1, 2, 6, 3, -2, 3, 10));
    assertEquals(scalar, Sqrt.of(RationalScalar.of(102, 7)));
  }

  @Test
  public void testNormalize() {
    TensorUnaryOperator tensorUnaryOperator = Normalize.with(StandardDeviation::ofVector);
    Tensor tensor = Tensors.vector(1, 5, 3, 7, 5, 2);
    Tensor result = tensorUnaryOperator.apply(tensor);
    Chop._14.requireClose(StandardDeviation.ofVector(result), RealScalar.ONE);
  }

  @Test
  public void testScalarFail() {
    AssertFail.of(() -> StandardDeviation.ofVector(RealScalar.ONE));
  }

  @Test
  public void testMatrixFail() {
    AssertFail.of(() -> StandardDeviation.ofVector(HilbertMatrix.of(3)));
  }
}
