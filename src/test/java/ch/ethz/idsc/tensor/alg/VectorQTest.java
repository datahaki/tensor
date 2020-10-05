// code by jph
package ch.ethz.idsc.tensor.alg;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class VectorQTest extends TestCase {
  public void testScalar() {
    assertFalse(VectorQ.of(RealScalar.ONE));
    assertFalse(VectorQ.of(ComplexScalar.I));
  }

  public void testVector() {
    assertTrue(VectorQ.of(Tensors.empty()));
    assertTrue(VectorQ.of(Tensors.vector(2, 3, 1)));
  }

  public void testVectorAndLength() {
    assertTrue(VectorQ.ofLength(Tensors.empty(), 0));
    assertFalse(VectorQ.ofLength(Tensors.empty(), 1));
    assertTrue(VectorQ.ofLength(Tensors.vector(2, 3, 1), 3));
    assertFalse(VectorQ.ofLength(Tensors.vector(2, 3, 1), 4));
    assertFalse(VectorQ.ofLength(IdentityMatrix.of(3), 3));
  }

  public void testMisc() {
    assertFalse(VectorQ.of(Tensors.fromString("{{1}}")));
    assertFalse(VectorQ.of(Tensors.fromString("{{1, 1, 3}, {7, 2, 9}}")));
    assertFalse(VectorQ.of(Tensors.fromString("{{1, 1}, {7, 2, 9}}")));
  }

  public void testAd() {
    assertFalse(VectorQ.of(Array.zeros(2, 3, 1)));
  }

  public void testRequire() {
    Tensor tensor = VectorQ.requireLength(Tensors.vector(1, 2, 3), 3);
    assertEquals(tensor, Tensors.vector(1, 2, 3));
  }

  public void testRequireFail() {
    AssertFail.of(() -> VectorQ.requireLength(Tensors.vector(1, 2, 3), 4));
    AssertFail.of(() -> VectorQ.requireLength(Tensors.vector(1, 2, 3), -3));
    AssertFail.of(() -> VectorQ.requireLength(RealScalar.ZERO, Scalar.LENGTH));
  }

  public void testEnsure() {
    Tensor empty = VectorQ.require(Tensors.empty());
    assertTrue(Tensors.isEmpty(empty));
    AssertFail.of(() -> VectorQ.require(HilbertMatrix.of(3)));
  }

  public void testFail() {
    AssertFail.of(() -> VectorQ.ofLength(Tensors.empty(), Scalar.LENGTH));
  }
}
