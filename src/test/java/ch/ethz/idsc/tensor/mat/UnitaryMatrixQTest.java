// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.LeviCivitaTensor;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class UnitaryMatrixQTest extends TestCase {
  public void testExample2d() {
    Tensor matrix = Tensors.fromString("{{1, I}, {I, 1}}").multiply(RealScalar.of(Math.sqrt(0.5)));
    assertTrue(UnitaryMatrixQ.of(matrix));
  }

  public void testExample3d() {
    Tensor matrix = Tensors.fromString("{{0.7071067811865476, 0.7071067811865476, 0.}, {-0.7071067811865476* I, 0.7071067811865476 *I, 0.}, {0., 0., I}}");
    assertTrue(UnitaryMatrixQ.of(matrix));
  }

  public void testFourier() {
    assertTrue(UnitaryMatrixQ.of(FourierMatrix.of(11)));
  }

  public void testOthers() {
    assertFalse(UnitaryMatrixQ.of(Tensors.fromString("{{1, 2}, {I, I}}")));
    assertFalse(UnitaryMatrixQ.of(RealScalar.of(3)));
    assertFalse(UnitaryMatrixQ.of(Tensors.vector(1, 2, 3)));
    assertFalse(UnitaryMatrixQ.of(LeviCivitaTensor.of(3)));
  }

  public void testRequire() {
    UnitaryMatrixQ.require(FourierMatrix.of(7), Chop._12);
    UnitaryMatrixQ.require(FourierMatrix.of(8));
    AssertFail.of(() -> UnitaryMatrixQ.require(Tensors.fromString("{{1, 2}, {I, I}}")));
  }
}
