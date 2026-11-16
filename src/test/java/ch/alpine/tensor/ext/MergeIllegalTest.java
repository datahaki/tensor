// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MergeIllegalTest {
  @Test
  void testSerializable() {
    Serialization.copy(MergeIllegal.operator());
  }

  @Test
  void testCollisionFail() {
    assertThrows(IllegalStateException.class, () -> MergeIllegal.operator().apply(null, null));
  }
}
