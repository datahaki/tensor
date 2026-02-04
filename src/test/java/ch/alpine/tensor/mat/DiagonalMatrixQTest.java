package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;

class DiagonalMatrixQTest {
  @Test
  void test() {
    assertTrue(DiagonalMatrixQ.of(DiagonalMatrix.of(1, 2, 3, 4)));
    assertFalse(DiagonalMatrixQ.of(DiagonalMatrix.of(1, 2, 3, 4).extract(0, 3)));
    assertFalse(DiagonalMatrixQ.of(HilbertMatrix.of(3)));
  }

  @Test
  void testRequire() {
    Tensor matrix = DiagonalMatrix.of(1, 2, 3, 4);
    assertTrue(matrix == DiagonalMatrixQ.require(matrix));
    assertThrows(Exception.class, () -> DiagonalMatrixQ.require(HilbertMatrix.of(3)));
  }
}
