// code by jph
package ch.alpine.tensor.nrm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Round;

class SoftmaxVectorTest {
  @Test
  void testMathematica() {
    Tensor tensor = Tensors.vector(0.1, 4.5, -0.2, 3.3, 5.4);
    Tensor actual = SoftmaxVector.of(tensor);
    Tensor expected = Tensors.vector(0.00324611, 0.264398, 0.00240478, 0.0796353, 0.650315);
    assertEquals(expected.subtract(actual).maps(Round.toMultipleOf(DoubleScalar.of(0.0001))), Array.zeros(5));
  }

  @Test
  void testSumOne() {
    Tensor tensor = Range.of(-3, 6);
    Tensor actual = SoftmaxVector.of(tensor);
    Chop._15.requireClose(Total.of(actual), RealScalar.ONE);
  }

  @Test
  void testEmptyFail() {
    assertThrows(NoSuchElementException.class, () -> SoftmaxVector.of(Tensors.empty()));
  }

  @Test
  void testScalarFail() {
    assertThrows(Throw.class, () -> SoftmaxVector.of(RealScalar.ONE));
  }

  @Test
  void testMatrixFail() {
    assertThrows(Exception.class, () -> SoftmaxVector.of(IdentityMatrix.of(3)));
  }
}
