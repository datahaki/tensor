// code by jph
package ch.alpine.tensor.ext;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.usr.AssertFail;

public class MergeIllegalTest {
  @Test
  public void testSerializable() throws ClassNotFoundException, IOException {
    Serialization.copy(MergeIllegal.operator());
  }

  @Test
  public void testCollisionFail() {
    AssertFail.of(() -> MergeIllegal.operator().apply(null, null));
  }
}
