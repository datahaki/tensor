// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TensorsPredicateTest {
  @Test
  void testIsEmpty() {
    assertFalse(Tensors.isEmpty(RealScalar.ONE));
    assertTrue(Tensors.isEmpty(Tensors.empty()));
    assertTrue(Tensors.isEmpty(Tensors.vector()));
    assertFalse(Tensors.isEmpty(Tensors.vector(1, 2, 3)));
  }

  @Test
  void testNonEmpty() {
    assertTrue(Tensors.nonEmpty(RealScalar.ONE));
    assertFalse(Tensors.nonEmpty(Tensors.empty()));
    assertFalse(Tensors.nonEmpty(Tensors.vector()));
    assertTrue(Tensors.nonEmpty(Tensors.vector(1, 2, 3)));
  }

  @Test
  void testIsUnmodifiable() {
    Tensor canwrite = Tensors.vector(1, 2, 3);
    Tensor readonly = canwrite.unmodifiable();
    assertFalse(Tensors.isUnmodifiable(canwrite));
    assertTrue(Tensors.isUnmodifiable(readonly));
    assertFalse(Tensors.isUnmodifiable(readonly.copy()));
  }
}
