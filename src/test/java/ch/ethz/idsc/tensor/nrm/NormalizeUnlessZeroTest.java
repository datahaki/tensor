// code by jph
package ch.ethz.idsc.tensor.nrm;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class NormalizeUnlessZeroTest extends TestCase {
  public void testNormalizeNaN() {
    Tensor vector = Tensors.of(RealScalar.ONE, DoubleScalar.INDETERMINATE, RealScalar.ONE);
    AssertFail.of(() -> NormalizeUnlessZero.with(VectorNorm2::of).apply(vector));
  }

  public void testNormalizeTotal() {
    TensorUnaryOperator tensorUnaryOperator = NormalizeUnlessZero.with(Total::ofVector);
    assertTrue(tensorUnaryOperator.toString().startsWith("NormalizeUnlessZero"));
    Tensor tensor = tensorUnaryOperator.apply(Tensors.vector(-1, 3, 2));
    assertEquals(tensor, Tensors.fromString("{-1/4, 3/4, 1/2}"));
    Tensor vector = Tensors.vector(-1, 3, -2);
    Tensor result = tensorUnaryOperator.apply(vector);
    assertEquals(vector, result);
    ExactTensorQ.require(result);
  }
}
