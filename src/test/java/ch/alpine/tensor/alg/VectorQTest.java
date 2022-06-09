// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;

class VectorQTest {
  @Test
  public void testScalar() {
    assertFalse(VectorQ.of(RealScalar.ONE));
    assertFalse(VectorQ.of(ComplexScalar.I));
  }

  @Test
  public void testVector() {
    assertTrue(VectorQ.of(Tensors.empty()));
    assertTrue(VectorQ.of(Tensors.vector(2, 3, 1)));
  }

  @Test
  public void testVectorAndLength() {
    assertTrue(VectorQ.ofLength(Tensors.empty(), 0));
    assertFalse(VectorQ.ofLength(Tensors.empty(), 1));
    assertTrue(VectorQ.ofLength(Tensors.vector(2, 3, 1), 3));
    assertFalse(VectorQ.ofLength(Tensors.vector(2, 3, 1), 4));
    assertFalse(VectorQ.ofLength(IdentityMatrix.of(3), 3));
  }

  @Test
  public void testMisc() {
    assertFalse(VectorQ.of(Tensors.fromString("{{1}}")));
    assertFalse(VectorQ.of(Tensors.fromString("{{1, 1, 3}, {7, 2, 9}}")));
    assertFalse(VectorQ.of(Tensors.fromString("{{1, 1}, {7, 2, 9}}")));
  }

  @Test
  public void testAd() {
    assertFalse(VectorQ.of(Array.zeros(2, 3, 1)));
  }

  @Test
  public void testRequire() {
    Tensor tensor = VectorQ.requireLength(Tensors.vector(1, 2, 3), 3);
    assertEquals(tensor, Tensors.vector(1, 2, 3));
  }

  @Test
  public void testRequireFail() {
    assertThrows(TensorRuntimeException.class, () -> VectorQ.requireLength(Tensors.vector(1, 2, 3), 4));
    assertThrows(TensorRuntimeException.class, () -> VectorQ.requireLength(Tensors.vector(1, 2, 3), -3));
    assertThrows(TensorRuntimeException.class, () -> VectorQ.requireLength(RealScalar.ZERO, Scalar.LENGTH));
  }

  @Test
  public void testEnsure() {
    Tensor empty = VectorQ.require(Tensors.empty());
    assertTrue(Tensors.isEmpty(empty));
    assertThrows(TensorRuntimeException.class, () -> VectorQ.require(HilbertMatrix.of(3)));
  }

  @Test
  public void testFail() {
    assertThrows(IllegalArgumentException.class, () -> VectorQ.ofLength(Tensors.empty(), Scalar.LENGTH));
  }
}
