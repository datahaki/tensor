// code by jph
package ch.alpine.tensor.sca.ply;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactTensorQ;

class HermiteHTest {
  @Test
  void testRecursion() {
    assertEquals(HermiteH.of(0).coeffs(), Tensors.vector(1));
    assertEquals(HermiteH.of(4).coeffs(), Tensors.vector(12, 0, -48, 0, 16));
    assertEquals(HermiteH.of(3).coeffs(), Tensors.vector(0, -12, 0, 8));
    assertEquals(HermiteH.of(2).coeffs(), Tensors.vector(-2, 0, 4));
    assertEquals(HermiteH.of(7).coeffs(), Tensors.vector(0, -1680, 0, 3360, 0, -1344, 0, 128));
    ExactTensorQ.require(HermiteH.of(40).coeffs());
  }

  @Test
  void testFail() {
    assertThrows(Exception.class, () -> HermiteH.of(-1));
  }
}
