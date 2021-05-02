// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.sca.N;
import junit.framework.TestCase;

public class NegativeSemidefiniteMatrixQTest extends TestCase {
  public void testDiagonal() {
    assertTrue(NegativeSemidefiniteMatrixQ.ofHermitian(DiagonalMatrix.of(-1, -3, -4)));
    assertTrue(NegativeSemidefiniteMatrixQ.ofHermitian(DiagonalMatrix.of(-1, 0, -4)));
  }

  public void testComplex() {
    assertTrue(NegativeSemidefiniteMatrixQ.ofHermitian(Tensors.fromString("{{-10, I}, {-I, -10}}")));
    assertTrue(NegativeSemidefiniteMatrixQ.ofHermitian(Tensors.fromString("{{-10, I}, {-I, -1/10}}")));
    assertTrue(NegativeSemidefiniteMatrixQ.ofHermitian(N.DOUBLE.of(Tensors.fromString("{{-10, I}, {-I, -10}}"))));
    assertTrue(NegativeSemidefiniteMatrixQ.ofHermitian(N.DOUBLE.of(Tensors.fromString("{{-10, I}, {-I, -1/10}}"))));
  }

  public void testVector() {
    assertFalse(NegativeSemidefiniteMatrixQ.ofHermitian(Tensors.vector(1, 2, 3)));
  }

  public void testRectangular() {
    assertFalse(NegativeSemidefiniteMatrixQ.ofHermitian(HilbertMatrix.of(2, 3)));
    assertFalse(NegativeSemidefiniteMatrixQ.ofHermitian(HilbertMatrix.of(3, 2)));
    assertFalse(NegativeSemidefiniteMatrixQ.ofHermitian(Array.zeros(3, 4)));
  }
}
