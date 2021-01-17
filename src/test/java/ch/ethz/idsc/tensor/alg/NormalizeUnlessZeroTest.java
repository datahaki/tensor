// code by jph
package ch.ethz.idsc.tensor.alg;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Frobenius;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class NormalizeUnlessZeroTest extends TestCase {
  public void testOk1() {
    Tensor v = Tensors.vector(0, 0, 0, 0);
    assertEquals(v, NormalizeUnlessZero.with(Norm._2::ofVector).apply(v));
    for (Norm n : Norm.values())
      assertEquals(v, NormalizeUnlessZero.with(n::ofVector).apply(v));
  }

  public void testEmpty() {
    for (Norm norm : Norm.values()) {
      TensorUnaryOperator tensorUnaryOperator = NormalizeUnlessZero.with(norm);
      Tensor tensor = Tensors.empty();
      AssertFail.of(() -> tensorUnaryOperator.apply(tensor));
    }
  }

  public void testNormalizeNaN() {
    Tensor vector = Tensors.of(RealScalar.ONE, DoubleScalar.INDETERMINATE, RealScalar.ONE);
    AssertFail.of(() -> NormalizeUnlessZero.with(Norm._2::ofVector).apply(vector));
  }

  public void testMatrixFail2() {
    TensorUnaryOperator normalize = NormalizeUnlessZero.with(Frobenius.NORM::ofVector);
    AssertFail.of(() -> normalize.apply(Array.zeros(3, 3)));
  }

  public void testNormalizeTotal() {
    TensorUnaryOperator tensorUnaryOperator = NormalizeUnlessZero.with(Total::ofVector);
    Tensor tensor = tensorUnaryOperator.apply(Tensors.vector(-1, 3, 2));
    assertEquals(tensor, Tensors.fromString("{-1/4, 3/4, 1/2}"));
    Tensor vector = Tensors.vector(-1, 3, -2);
    Tensor result = tensorUnaryOperator.apply(vector);
    assertEquals(vector, result);
    ExactTensorQ.require(result);
  }
}
