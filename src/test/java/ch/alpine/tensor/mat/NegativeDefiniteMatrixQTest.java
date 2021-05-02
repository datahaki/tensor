// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.sca.N;
import junit.framework.TestCase;

public class NegativeDefiniteMatrixQTest extends TestCase {
  public void testDiagonal() {
    assertTrue(NegativeDefiniteMatrixQ.ofHermitian(DiagonalMatrix.of(-1, -3, -4)));
    assertFalse(NegativeDefiniteMatrixQ.ofHermitian(DiagonalMatrix.of(-1, 0, -4)));
  }

  public void testComplex() {
    assertTrue(NegativeDefiniteMatrixQ.ofHermitian(Tensors.fromString("{{-10, I}, {-I, -10}}")));
    assertFalse(NegativeDefiniteMatrixQ.ofHermitian(Tensors.fromString("{{-10, I}, {-I, -1/10}}")));
    assertTrue(NegativeDefiniteMatrixQ.ofHermitian(N.DOUBLE.of(Tensors.fromString("{{-10, I}, {-I, -10}}"))));
    assertFalse(NegativeDefiniteMatrixQ.ofHermitian(N.DOUBLE.of(Tensors.fromString("{{-10, I}, {-I, -1/10}}"))));
  }

  public void testVector() {
    assertFalse(NegativeDefiniteMatrixQ.ofHermitian(Tensors.vector(1, 2, 3)));
  }

  public void testRectangular() {
    assertFalse(NegativeDefiniteMatrixQ.ofHermitian(HilbertMatrix.of(2, 3)));
    assertFalse(NegativeDefiniteMatrixQ.ofHermitian(HilbertMatrix.of(3, 2)));
    assertFalse(NegativeDefiniteMatrixQ.ofHermitian(Array.zeros(3, 4)));
  }
}
