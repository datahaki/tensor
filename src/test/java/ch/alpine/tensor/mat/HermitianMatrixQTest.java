// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class HermitianMatrixQTest extends TestCase {
  public void testMatrix() {
    assertTrue(HermitianMatrixQ.of(Tensors.fromString("{{0, I}, {-I, 0}}")));
    assertFalse(HermitianMatrixQ.of(Tensors.fromString("{{I, I}, {-I, 0}}")));
    assertFalse(HermitianMatrixQ.of(Tensors.fromString("{{0, I}, {I, 0}}")));
  }

  public void testHilbert() {
    assertTrue(HermitianMatrixQ.of(HilbertMatrix.of(10)));
  }

  public void testRectangular() {
    assertFalse(HermitianMatrixQ.of(Array.zeros(2, 3, 3)));
    assertFalse(HermitianMatrixQ.of(HilbertMatrix.of(3, 4)));
  }

  public void testNaN() {
    assertFalse(HermitianMatrixQ.of(ConstantArray.of(DoubleScalar.INDETERMINATE, 3, 3)));
  }

  public void testNonMatrix() {
    assertFalse(HermitianMatrixQ.of(Tensors.vector(1, 2, 3)));
    assertFalse(HermitianMatrixQ.of(RealScalar.ONE));
  }

  public void testRequire() {
    HermitianMatrixQ.require(HilbertMatrix.of(10));
    AssertFail.of(() -> HermitianMatrixQ.require(Tensors.vector(1, 2, 3)));
  }

  public void testRequireChop() {
    AssertFail.of(() -> HermitianMatrixQ.require(Tensors.vector(1, 2, 3), Chop._02));
  }
}
