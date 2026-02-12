// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.fft.Fourier;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.sca.Chop;

class UnitaryMatrixQTest {
  @Test
  void testExample2d() {
    Tensor matrix = Tensors.fromString("{{1, I}, {I, 1}}").multiply(RealScalar.of(Math.sqrt(0.5)));
    assertTrue(UnitaryMatrixQ.INSTANCE.test(matrix));
  }

  @Test
  void testExample3d() {
    Tensor matrix = Tensors.fromString("{{0.7071067811865476, 0.7071067811865476, 0.}, {-0.7071067811865476* I, 0.7071067811865476 *I, 0.}, {0., 0., I}}");
    assertTrue(UnitaryMatrixQ.INSTANCE.test(matrix));
  }

  @Test
  void testNonSquare() {
    assertFalse(UnitaryMatrixQ.INSTANCE.test(HilbertMatrix.of(2, 3)));
    assertFalse(UnitaryMatrixQ.INSTANCE.test(HilbertMatrix.of(3, 2)));
  }

  @Test
  void testFourier() {
    assertTrue(UnitaryMatrixQ.INSTANCE.test(Fourier.FORWARD.matrix(11)));
  }

  @Test
  void testOthers() {
    assertFalse(UnitaryMatrixQ.INSTANCE.test(Tensors.fromString("{{1, 2}, {I, I}}")));
    assertFalse(UnitaryMatrixQ.INSTANCE.test(RealScalar.of(3)));
    assertFalse(UnitaryMatrixQ.INSTANCE.test(Tensors.vector(1, 2, 3)));
    assertFalse(UnitaryMatrixQ.INSTANCE.test(LeviCivitaTensor.of(3)));
  }

  @Test
  void testRequire() {
    new UnitaryMatrixQ(Chop._12).require(Fourier.FORWARD.matrix(7));
    UnitaryMatrixQ.INSTANCE.require(Fourier.FORWARD.matrix(8));
    assertThrows(Throw.class, () -> UnitaryMatrixQ.INSTANCE.require(Tensors.fromString("{{1, 2}, {I, I}}")));
  }
}
