// code by jph
package ch.alpine.tensor.chq;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;

class FiniteTensorQTest {
  @Test
  void testOf() {
    assertTrue(FiniteTensorQ.of(Tensors.vector(1, 1, 1.)));
    assertTrue(FiniteTensorQ.of(Tensors.vector(1, 2, 3)));
  }

  @Test
  void testRequire() {
    Tensor xyz = Tensors.vector(1, 2, 3);
    assertEquals(FiniteTensorQ.require(xyz), xyz);
  }

  @Test
  void testRequireFail() {
    assertThrows(TensorRuntimeException.class, () -> FiniteTensorQ.require(Tensors.vector(1, Double.NaN, 1.)));
    assertThrows(TensorRuntimeException.class, () -> FiniteTensorQ.require(Tensors.vector(1, Double.POSITIVE_INFINITY)));
  }
}
