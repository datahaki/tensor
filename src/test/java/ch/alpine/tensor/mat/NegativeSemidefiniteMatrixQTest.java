// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.sca.N;

class NegativeSemidefiniteMatrixQTest {
  @Test
  public void testDiagonal() {
    assertTrue(NegativeSemidefiniteMatrixQ.ofHermitian(DiagonalMatrix.of(-1, -3, -4)));
    assertTrue(NegativeSemidefiniteMatrixQ.ofHermitian(DiagonalMatrix.of(-1, 0, -4)));
  }

  @Test
  public void testComplex() {
    assertTrue(NegativeSemidefiniteMatrixQ.ofHermitian(Tensors.fromString("{{-10, I}, {-I, -10}}")));
    assertTrue(NegativeSemidefiniteMatrixQ.ofHermitian(Tensors.fromString("{{-10, I}, {-I, -1/10}}")));
    assertTrue(NegativeSemidefiniteMatrixQ.ofHermitian(N.DOUBLE.of(Tensors.fromString("{{-10, I}, {-I, -10}}"))));
    assertTrue(NegativeSemidefiniteMatrixQ.ofHermitian(N.DOUBLE.of(Tensors.fromString("{{-10, I}, {-I, -1/10}}"))));
  }

  @Test
  public void testVector() {
    assertFalse(NegativeSemidefiniteMatrixQ.ofHermitian(Tensors.vector(1, 2, 3)));
  }

  @Test
  public void testRectangular() {
    assertFalse(NegativeSemidefiniteMatrixQ.ofHermitian(HilbertMatrix.of(2, 3)));
    assertFalse(NegativeSemidefiniteMatrixQ.ofHermitian(HilbertMatrix.of(3, 2)));
    assertFalse(NegativeSemidefiniteMatrixQ.ofHermitian(Array.zeros(3, 4)));
  }
}
