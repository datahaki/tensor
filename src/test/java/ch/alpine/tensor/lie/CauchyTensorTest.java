// code by jph
package ch.alpine.tensor.lie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.ArrayQ;
import ch.alpine.tensor.mat.HilbertMatrix;

class CauchyTensorTest {
  private static void _check(int n) {
    Tensor hilbert = HilbertMatrix.of(n);
    Tensor matrix = CauchyTensor.of(Tensors.vector(i -> RationalScalar.of(2 * i + 1, 2), n), 2);
    assertEquals(matrix, hilbert);
  }

  @Test
  void testHilbert() {
    for (int n = 1; n < 6; ++n)
      _check(n);
  }

  @Test
  void testRank() {
    assertTrue(ArrayQ.ofRank(CauchyTensor.of(Tensors.vector(1, 2, 3, 4), 3), 3));
    assertTrue(ArrayQ.ofRank(CauchyTensor.of(Tensors.vector(1, 2, 3), 4), 4));
  }

  @Test
  void testFail() {
    assertThrows(IllegalArgumentException.class, () -> CauchyTensor.of(RealScalar.ONE, 1));
    assertThrows(ClassCastException.class, () -> CauchyTensor.of(Tensors.fromString("{{1, 2}}"), 1));
  }
}
