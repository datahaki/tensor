// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.sca.N;

class PositiveDefiniteMatrixQTest {
  @Test
  void testMathematica2() {
    boolean status = PositiveDefiniteMatrixQ.ofHermitian(Tensors.fromString("{{8, 3}, {3, 8}}"));
    assertTrue(status);
  }

  @Test
  void testMathematica3() {
    boolean status = PositiveDefiniteMatrixQ.ofHermitian(Tensors.fromString("{{4, 3, 2, I}, {3, 4, 3, 2}, {2, 3, 4, 3}, {-I, 2, 3, 4}}"));
    assertTrue(status);
  }

  @Test
  void testMathematica4() {
    boolean status = PositiveDefiniteMatrixQ.ofHermitian(Tensors.fromString("{{4, 3, 2, I}, {3, 4, 3, 2}, {2, 3, 4, 3}, {-I, 2, 3, 0}}"));
    assertFalse(status);
  }

  @Test
  void testZeros() {
    assertFalse(PositiveDefiniteMatrixQ.ofHermitian(Array.zeros(4, 4)));
  }

  @Test
  void testComplex() {
    assertTrue(PositiveDefiniteMatrixQ.ofHermitian(Tensors.fromString("{{10, I}, {-I, 10}}")));
    assertFalse(PositiveDefiniteMatrixQ.ofHermitian(Tensors.fromString("{{10, I}, {-I, 1/10}}")));
    assertTrue(PositiveDefiniteMatrixQ.ofHermitian(N.DOUBLE.of(Tensors.fromString("{{10, I}, {-I, 10}}"))));
    assertFalse(PositiveDefiniteMatrixQ.ofHermitian(N.DOUBLE.of(Tensors.fromString("{{10, I}, {-I, 1/10}}"))));
  }

  @Test
  void testVector() {
    assertFalse(PositiveDefiniteMatrixQ.ofHermitian(Tensors.vector(1, 2, 3)));
  }

  @Test
  void testRectangular() {
    assertFalse(PositiveDefiniteMatrixQ.ofHermitian(HilbertMatrix.of(2, 3)));
    assertFalse(PositiveDefiniteMatrixQ.ofHermitian(HilbertMatrix.of(3, 2)));
    assertFalse(PositiveDefiniteMatrixQ.ofHermitian(Array.zeros(3, 4)));
  }
}
