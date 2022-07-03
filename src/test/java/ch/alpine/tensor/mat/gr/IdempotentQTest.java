// code by jph
package ch.alpine.tensor.mat.gr;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.HilbertMatrix;

class IdempotentQTest {
  @Test
  void testSimple() {
    Tensor matrix = Tensors.of(UnitVector.of(2, 1), UnitVector.of(2, 1));
    assertTrue(IdempotentQ.of(matrix));
  }

  @Test
  void testFalse() {
    assertFalse(IdempotentQ.of(DiagonalMatrix.of(1, 2)));
    assertFalse(IdempotentQ.of(HilbertMatrix.of(2, 3)));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> IdempotentQ.of(null));
  }
}
