// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.sca.Chop;

class HermitianMatrixQTest {
  @Test
  void testMatrix() {
    assertTrue(HermitianMatrixQ.INSTANCE.test(Tensors.fromString("{{0, I}, {-I, 0}}")));
    assertFalse(HermitianMatrixQ.INSTANCE.test(Tensors.fromString("{{I, I}, {-I, 0}}")));
    assertFalse(HermitianMatrixQ.INSTANCE.test(Tensors.fromString("{{0, I}, {I, 0}}")));
  }

  @Test
  void testHilbert() {
    assertTrue(HermitianMatrixQ.INSTANCE.test(HilbertMatrix.of(10)));
  }

  @Test
  void testRectangular() {
    assertFalse(HermitianMatrixQ.INSTANCE.test(Array.zeros(2, 3, 3)));
    assertFalse(HermitianMatrixQ.INSTANCE.test(HilbertMatrix.of(3, 4)));
  }

  @Test
  void testNaN() {
    assertFalse(HermitianMatrixQ.INSTANCE.test(ConstantArray.of(DoubleScalar.INDETERMINATE, 3, 3)));
  }

  @Test
  void testNonMatrix() {
    assertFalse(HermitianMatrixQ.INSTANCE.test(Tensors.vector(1, 2, 3)));
    assertFalse(HermitianMatrixQ.INSTANCE.test(RealScalar.ONE));
  }

  @Test
  void testRequire() {
    assertEquals(HermitianMatrixQ.INSTANCE.require(HilbertMatrix.of(10)), HilbertMatrix.of(10));
  }

  @Test
  void testRequireFail() {
    assertThrows(Throw.class, () -> HermitianMatrixQ.INSTANCE.require(Tensors.vector(1, 2, 3)));
  }

  @Test
  void testRequireChop() {
    assertThrows(Throw.class, () -> new HermitianMatrixQ(Chop._02).require(Tensors.vector(1, 2, 3)));
  }
}
