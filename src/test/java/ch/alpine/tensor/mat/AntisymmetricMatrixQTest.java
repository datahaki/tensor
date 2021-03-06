// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.lie.Cross;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class AntisymmetricMatrixQTest extends TestCase {
  public void testMatrix() {
    assertTrue(AntisymmetricMatrixQ.of(Array.zeros(4, 4)));
    assertFalse(AntisymmetricMatrixQ.of(IdentityMatrix.of(3)));
    assertTrue(AntisymmetricMatrixQ.of(Tensors.fromString("{{0, 1}, {-1, 0}}")));
  }

  public void testRectangularMatrix() {
    assertFalse(AntisymmetricMatrixQ.of(Array.zeros(2, 4)));
    assertFalse(AntisymmetricMatrixQ.of(HilbertMatrix.of(2, 3)));
  }

  public void testCross() {
    assertTrue(AntisymmetricMatrixQ.of(Cross.skew3(Tensors.vector(1, 2, 3))));
  }

  public void testNonMatrix() {
    assertFalse(AntisymmetricMatrixQ.of(RealScalar.ONE));
    assertFalse(AntisymmetricMatrixQ.of(LeviCivitaTensor.of(3)));
  }

  public void testRequire() {
    AntisymmetricMatrixQ.require(Tensors.fromString("{{0, 2}, {-2, 0}}"));
    AssertFail.of(() -> AntisymmetricMatrixQ.require(Tensors.fromString("{{0, 2}, {-1, 0}}")));
  }
}
