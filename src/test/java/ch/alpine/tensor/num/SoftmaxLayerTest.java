// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class SoftmaxLayerTest extends TestCase {
  public void testMathematica() {
    Tensor tensor = Tensors.vector(0.1, 4.5, -0.2, 3.3, 5.4);
    Tensor actual = SoftmaxLayer.of(tensor);
    Tensor expected = Tensors.vector(0.00324611, 0.264398, 0.00240478, 0.0796353, 0.650315);
    assertEquals(expected.subtract(actual).map(Round.toMultipleOf(DoubleScalar.of(0.0001))), Array.zeros(5));
  }

  public void testSumOne() {
    Tensor tensor = Range.of(-3, 6);
    Tensor actual = SoftmaxLayer.of(tensor);
    Chop._15.requireClose(Total.of(actual), RealScalar.ONE);
  }

  public void testEmptyFail() {
    AssertFail.of(() -> SoftmaxLayer.of(Tensors.empty()));
  }

  public void testScalarFail() {
    AssertFail.of(() -> SoftmaxLayer.of(RealScalar.ONE));
  }
}
