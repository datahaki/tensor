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
    assertTrue(AntisymmetricMatrixQ.INSTANCE.isMember(Array.zeros(4, 4)));
    assertFalse(AntisymmetricMatrixQ.INSTANCE.isMember(IdentityMatrix.of(3)));
    assertTrue(AntisymmetricMatrixQ.INSTANCE.isMember(Tensors.fromString("{{0, 1}, {-1, 0}}")));
  }

  @Test
  void testRectangularMatrix() {
    assertFalse(AntisymmetricMatrixQ.INSTANCE.isMember(Array.zeros(2, 4)));
    assertFalse(AntisymmetricMatrixQ.INSTANCE.isMember(HilbertMatrix.of(2, 3)));
  }

  @Test
  void testCross() {
    assertTrue(AntisymmetricMatrixQ.INSTANCE.isMember(Cross.skew3(Tensors.vector(1, 2, 3))));
  }

  @Test
  void testNonMatrix() {
    assertFalse(AntisymmetricMatrixQ.INSTANCE.isMember(RealScalar.ONE));
    assertFalse(AntisymmetricMatrixQ.INSTANCE.isMember(LeviCivitaTensor.of(3)));
  }

  @Test
  void testRequire() {
    Tensor matrix = Tensors.fromString("{{0, 2}, {-2, 0}}");
    assertEquals(AntisymmetricMatrixQ.INSTANCE.requireMember(matrix), matrix);
    assertThrows(Throw.class, () -> AntisymmetricMatrixQ.INSTANCE.requireMember(Tensors.fromString("{{0, 2}, {-1, 0}}")));
  }
}
