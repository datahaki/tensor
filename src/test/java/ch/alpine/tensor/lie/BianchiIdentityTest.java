// code by jph
package ch.alpine.tensor.lie;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.mat.HilbertMatrix;

class BianchiIdentityTest {
  @Test
  void testRequireTrivial() {
    BianchiIdentity.require(Array.zeros(4, 4, 4, 4));
  }

  @Test
  void testRank1Fail() {
    assertThrows(Exception.class, () -> BianchiIdentity.of(Tensors.vector(1, 2, 3)));
  }

  @Test
  void testRank2Fail() {
    assertThrows(Exception.class, () -> BianchiIdentity.of(HilbertMatrix.of(3, 3)));
  }

  @Test
  void testRank3Fail() {
    assertThrows(Exception.class, () -> BianchiIdentity.of(Array.zeros(3, 3, 3)));
  }
}
