// code by jph
package ch.alpine.tensor.lie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.mat.SymmetricMatrixQ;

class HankelTensorTest {
  @Test
  void testRank2() {
    Tensor tensor = HankelTensor.of(Tensors.vector(1, 2, 3, 4, 5), 2);
    SymmetricMatrixQ.INSTANCE.requireMember(tensor);
  }

  @Test
  void testRank3a() {
    Tensor tensor = HankelTensor.of(Tensors.vector(1, 2, 3, 4), 3);
    tensor.forEach(SymmetricMatrixQ.INSTANCE::requireMember);
  }

  @Test
  void testRank3b() {
    Tensor tensor = HankelTensor.of(Tensors.vector(0, 1, 2, 3, 4, 5, 6), 3);
    tensor.forEach(SymmetricMatrixQ.INSTANCE::requireMember);
  }

  @Test
  void testRank4() {
    Tensor tensor = HankelTensor.of(Tensors.vector(1, 2, 3, 4, 5), 4);
    Dimensions dimensions = new Dimensions(tensor);
    assertTrue(dimensions.isArray());
    assertEquals(dimensions.rank(), 4);
  }

  @Test
  void testFailVector() {
    assertThrows(IllegalArgumentException.class, () -> HankelTensor.of(RealScalar.ONE, 1));
    assertThrows(ClassCastException.class, () -> HankelTensor.of(Tensors.fromString("{{1, 2}}"), 1));
  }

  @Test
  void testFailRank() {
    assertThrows(Throw.class, () -> HankelTensor.of(Tensors.vector(1, 2, 3, 4, 5, 6), 2));
  }
}
