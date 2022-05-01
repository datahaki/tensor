// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.sca.Chop;

class HermitianMatrixQTest {
  @Test
  public void testMatrix() {
    assertTrue(HermitianMatrixQ.of(Tensors.fromString("{{0, I}, {-I, 0}}")));
    assertFalse(HermitianMatrixQ.of(Tensors.fromString("{{I, I}, {-I, 0}}")));
    assertFalse(HermitianMatrixQ.of(Tensors.fromString("{{0, I}, {I, 0}}")));
  }

  @Test
  public void testHilbert() {
    assertTrue(HermitianMatrixQ.of(HilbertMatrix.of(10)));
  }

  @Test
  public void testRectangular() {
    assertFalse(HermitianMatrixQ.of(Array.zeros(2, 3, 3)));
    assertFalse(HermitianMatrixQ.of(HilbertMatrix.of(3, 4)));
  }

  @Test
  public void testNaN() {
    assertFalse(HermitianMatrixQ.of(ConstantArray.of(DoubleScalar.INDETERMINATE, 3, 3)));
  }

  @Test
  public void testNonMatrix() {
    assertFalse(HermitianMatrixQ.of(Tensors.vector(1, 2, 3)));
    assertFalse(HermitianMatrixQ.of(RealScalar.ONE));
  }

  @Test
  public void testRequire() {
    HermitianMatrixQ.require(HilbertMatrix.of(10));
    assertThrows(TensorRuntimeException.class, () -> HermitianMatrixQ.require(Tensors.vector(1, 2, 3)));
  }

  @Test
  public void testRequireChop() {
    assertThrows(TensorRuntimeException.class, () -> HermitianMatrixQ.require(Tensors.vector(1, 2, 3), Chop._02));
  }
}
