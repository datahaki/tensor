// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.fft.FourierMatrix;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.sca.Chop;

class UnitaryMatrixQTest {
  @Test
  void testExample2d() {
    Tensor matrix = Tensors.fromString("{{1, I}, {I, 1}}").multiply(RealScalar.of(Math.sqrt(0.5)));
    assertTrue(UnitaryMatrixQ.of(matrix));
  }

  @Test
  void testExample3d() {
    Tensor matrix = Tensors.fromString("{{0.7071067811865476, 0.7071067811865476, 0.}, {-0.7071067811865476* I, 0.7071067811865476 *I, 0.}, {0., 0., I}}");
    assertTrue(UnitaryMatrixQ.of(matrix));
  }

  @Test
  void testFourier() {
    assertTrue(UnitaryMatrixQ.of(FourierMatrix.of(11)));
  }

  @Test
  void testOthers() {
    assertFalse(UnitaryMatrixQ.of(Tensors.fromString("{{1, 2}, {I, I}}")));
    assertFalse(UnitaryMatrixQ.of(RealScalar.of(3)));
    assertFalse(UnitaryMatrixQ.of(Tensors.vector(1, 2, 3)));
    assertFalse(UnitaryMatrixQ.of(LeviCivitaTensor.of(3)));
  }

  @Test
  void testRequire() {
    UnitaryMatrixQ.require(FourierMatrix.of(7), Chop._12);
    UnitaryMatrixQ.require(FourierMatrix.of(8));
    assertThrows(TensorRuntimeException.class, () -> UnitaryMatrixQ.require(Tensors.fromString("{{1, 2}, {I, I}}")));
  }
}
