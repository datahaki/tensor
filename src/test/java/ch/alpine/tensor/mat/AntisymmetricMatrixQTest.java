// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.lie.rot.Cross;

class AntisymmetricMatrixQTest {
  @Test
  void testMatrix() {
    assertTrue(AntisymmetricMatrixQ.INSTANCE.test(Array.zeros(4, 4)));
    assertFalse(AntisymmetricMatrixQ.INSTANCE.test(IdentityMatrix.of(3)));
    assertTrue(AntisymmetricMatrixQ.INSTANCE.test(Tensors.fromString("{{0, 1}, {-1, 0}}")));
  }

  @Test
  void testRectangularMatrix() {
    assertFalse(AntisymmetricMatrixQ.INSTANCE.test(Array.zeros(2, 4)));
    assertFalse(AntisymmetricMatrixQ.INSTANCE.test(HilbertMatrix.of(2, 3)));
  }

  @Test
  void testCross() {
    assertTrue(AntisymmetricMatrixQ.INSTANCE.test(Cross.skew3(Tensors.vector(1, 2, 3))));
  }

  @Test
  void testNonMatrix() {
    assertFalse(AntisymmetricMatrixQ.INSTANCE.test(RealScalar.ONE));
    assertFalse(AntisymmetricMatrixQ.INSTANCE.test(LeviCivitaTensor.of(3)));
  }

  @Test
  void testRequire() {
    Tensor matrix = Tensors.fromString("{{0, 2}, {-2, 0}}");
    assertEquals(AntisymmetricMatrixQ.INSTANCE.require(matrix), matrix);
    assertThrows(Throw.class, () -> AntisymmetricMatrixQ.INSTANCE.require(Tensors.fromString("{{0, 2}, {-1, 0}}")));
  }
}
