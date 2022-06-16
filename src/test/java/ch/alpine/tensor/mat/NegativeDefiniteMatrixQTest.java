// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.sca.N;

class NegativeDefiniteMatrixQTest {
  @Test
  void testDiagonal() {
    assertTrue(NegativeDefiniteMatrixQ.ofHermitian(DiagonalMatrix.of(-1, -3, -4)));
    assertFalse(NegativeDefiniteMatrixQ.ofHermitian(DiagonalMatrix.of(-1, 0, -4)));
  }

  @Test
  void testComplex() {
    assertTrue(NegativeDefiniteMatrixQ.ofHermitian(Tensors.fromString("{{-10, I}, {-I, -10}}")));
    assertFalse(NegativeDefiniteMatrixQ.ofHermitian(Tensors.fromString("{{-10, I}, {-I, -1/10}}")));
    assertTrue(NegativeDefiniteMatrixQ.ofHermitian(N.DOUBLE.of(Tensors.fromString("{{-10, I}, {-I, -10}}"))));
    assertFalse(NegativeDefiniteMatrixQ.ofHermitian(N.DOUBLE.of(Tensors.fromString("{{-10, I}, {-I, -1/10}}"))));
  }

  @Test
  void testVector() {
    assertFalse(NegativeDefiniteMatrixQ.ofHermitian(Tensors.vector(1, 2, 3)));
  }

  @Test
  void testRectangular() {
    assertFalse(NegativeDefiniteMatrixQ.ofHermitian(HilbertMatrix.of(2, 3)));
    assertFalse(NegativeDefiniteMatrixQ.ofHermitian(HilbertMatrix.of(3, 2)));
    assertFalse(NegativeDefiniteMatrixQ.ofHermitian(Array.zeros(3, 4)));
  }
}
