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
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.ZeroDefectArrayQ;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.pi.LinearSubspace;

class VectorQTest {
  @Test
  void testScalar() {
    assertFalse(VectorQ.of(RealScalar.ONE));
    assertFalse(VectorQ.of(ComplexScalar.I));
  }

  @Test
  void testVector() {
    assertTrue(VectorQ.of(Tensors.empty()));
    assertTrue(VectorQ.of(Tensors.vector(2, 3, 1)));
  }

  @Test
  void testVectorAndLength() {
    assertTrue(VectorQ.ofLength(Tensors.empty(), 0));
    assertFalse(VectorQ.ofLength(Tensors.empty(), 1));
    assertTrue(VectorQ.ofLength(Tensors.vector(2, 3, 1), 3));
    assertFalse(VectorQ.ofLength(Tensors.vector(2, 3, 1), 4));
    assertFalse(VectorQ.ofLength(IdentityMatrix.of(3), 3));
  }

  @Test
  void testMisc() {
    assertFalse(VectorQ.of(Tensors.fromString("{{1}}")));
    assertFalse(VectorQ.of(Tensors.fromString("{{1, 1, 3}, {7, 2, 9}}")));
    assertFalse(VectorQ.of(Tensors.fromString("{{1, 1}, {7, 2, 9}}")));
  }

  @Test
  void testAd() {
    assertFalse(VectorQ.of(Array.zeros(2, 3, 1)));
  }

  @Test
  void testRequire() {
    Tensor tensor = VectorQ.requireLength(Tensors.vector(1, 2, 3), 3);
    assertEquals(tensor, Tensors.vector(1, 2, 3));
  }

  @Test
  void testRequireFail() {
    assertThrows(Throw.class, () -> VectorQ.requireLength(Tensors.vector(1, 2, 3), 4));
    assertThrows(Throw.class, () -> VectorQ.requireLength(Tensors.vector(1, 2, 3), -3));
    assertThrows(Throw.class, () -> VectorQ.requireLength(RealScalar.ZERO, Scalar.LENGTH));
  }

  @Test
  void testEnsure() {
    Tensor empty = VectorQ.require(Tensors.empty());
    assertTrue(Tensors.isEmpty(empty));
    assertThrows(Throw.class, () -> VectorQ.require(HilbertMatrix.of(3)));
  }

  @Test
  void testSubspace() {
    int n = 3;
    ZeroDefectArrayQ zdaq = VectorQ.ofLength(n);
    LinearSubspace linearSubspace = LinearSubspace.of(zdaq::defect, n);
    assertEquals(linearSubspace.dimensions(), n);
    assertTrue(linearSubspace.basis().stream().allMatch(zdaq));
  }

  @Test
  void testSubspaceGen() {
    int n = 5;
    ZeroDefectArrayQ zdaq = VectorQ.INSTANCE;
    LinearSubspace linearSubspace = LinearSubspace.of(zdaq::defect, n);
    assertEquals(linearSubspace.dimensions(), n);
    assertTrue(linearSubspace.basis().stream().allMatch(zdaq));
  }

  @Test
  void testFail() {
    assertThrows(IllegalArgumentException.class, () -> VectorQ.ofLength(Tensors.empty(), Scalar.LENGTH));
  }
}
