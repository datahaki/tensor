// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class MergeIllegalTest {
  @Test
  public void testSerializable() throws ClassNotFoundException, IOException {
    Serialization.copy(MergeIllegal.operator());
  }

  @Test
  public void testCollisionFail() {
    assertThrows(IllegalStateException.class, () -> MergeIllegal.operator().apply(null, null));
  }
}
