// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.sca.N;

class PositiveSemidefiniteMatrixQTest {
  @Test
  void testDiagonal() {
    assertTrue(PositiveSemidefiniteMatrixQ.ofHermitian(DiagonalMatrix.of(3, 2, 0, 1)));
    assertFalse(PositiveSemidefiniteMatrixQ.ofHermitian(DiagonalMatrix.of(3, -2, 0, 1)));
  }

  @Test
  void testZeros() {
    assertTrue(PositiveSemidefiniteMatrixQ.ofHermitian(Array.zeros(4, 4)));
  }

  @Test
  void testComplex() {
    assertTrue(PositiveSemidefiniteMatrixQ.ofHermitian(Tensors.fromString("{{10, I}, {-I, 10}}")));
    assertTrue(PositiveSemidefiniteMatrixQ.ofHermitian(Tensors.fromString("{{10, I}, {-I, 1/10}}")));
    assertTrue(PositiveSemidefiniteMatrixQ.ofHermitian(Tensors.fromString("{{10, I}, {-I, 10}}").maps(N.DOUBLE)));
    assertTrue(PositiveSemidefiniteMatrixQ.ofHermitian(Tensors.fromString("{{10, I}, {-I, 1/10}}").maps(N.DOUBLE)));
  }

  @Test
  void testVector() {
    assertFalse(PositiveSemidefiniteMatrixQ.ofHermitian(Tensors.vector(1, 2, 3)));
  }

  @Test
  void testRectangular() {
    assertFalse(PositiveSemidefiniteMatrixQ.ofHermitian(HilbertMatrix.of(2, 3)));
    assertFalse(PositiveSemidefiniteMatrixQ.ofHermitian(HilbertMatrix.of(3, 2)));
    assertFalse(PositiveSemidefiniteMatrixQ.ofHermitian(Array.zeros(3, 4)));
  }
}
