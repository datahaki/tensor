// code by jph
package ch.alpine.tensor.lie.r2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.chq.ExactTensorQ;

class PolygonalNumberTest {
  @Test
  public void testSimple() {
    Tensor tensor = Range.of(0, 8).map(PolygonalNumber::of);
    Tensor expect = Tensors.vector(0, 1, 3, 6, 10, 15, 21, 28);
    assertEquals(tensor, expect);
    ExactTensorQ.require(tensor);
  }

  @Test
  public void testBivar() {
    Tensor tensor = Range.of(0, 8).map(s -> PolygonalNumber.of(RealScalar.of(5), s));
    Tensor expect = Tensors.vector(0, 1, 5, 12, 22, 35, 51, 70);
    assertEquals(tensor, expect);
    ExactTensorQ.require(tensor);
  }
}
