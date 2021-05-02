// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.sca.N;
import junit.framework.TestCase;

public class PositiveSemidefiniteMatrixQTest extends TestCase {
  public void testDiagonal() {
    assertTrue(PositiveSemidefiniteMatrixQ.ofHermitian(DiagonalMatrix.of(3, 2, 0, 1)));
    assertFalse(PositiveSemidefiniteMatrixQ.ofHermitian(DiagonalMatrix.of(3, -2, 0, 1)));
  }

  public void testZeros() {
    assertTrue(PositiveSemidefiniteMatrixQ.ofHermitian(Array.zeros(4, 4)));
  }

  public void testComplex() {
    assertTrue(PositiveSemidefiniteMatrixQ.ofHermitian(Tensors.fromString("{{10, I}, {-I, 10}}")));
    assertTrue(PositiveSemidefiniteMatrixQ.ofHermitian(Tensors.fromString("{{10, I}, {-I, 1/10}}")));
    assertTrue(PositiveSemidefiniteMatrixQ.ofHermitian(N.DOUBLE.of(Tensors.fromString("{{10, I}, {-I, 10}}"))));
    assertTrue(PositiveSemidefiniteMatrixQ.ofHermitian(N.DOUBLE.of(Tensors.fromString("{{10, I}, {-I, 1/10}}"))));
  }

  public void testVector() {
    assertFalse(PositiveSemidefiniteMatrixQ.ofHermitian(Tensors.vector(1, 2, 3)));
  }

  public void testRectangular() {
    assertFalse(PositiveSemidefiniteMatrixQ.ofHermitian(HilbertMatrix.of(2, 3)));
    assertFalse(PositiveSemidefiniteMatrixQ.ofHermitian(HilbertMatrix.of(3, 2)));
    assertFalse(PositiveSemidefiniteMatrixQ.ofHermitian(Array.zeros(3, 4)));
  }
}
