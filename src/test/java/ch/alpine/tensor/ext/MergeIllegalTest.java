// code by jph
package ch.alpine.tensor.ext;

import java.io.IOException;

import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class MergeIllegalTest extends TestCase {
  public void testSerializable() throws ClassNotFoundException, IOException {
    Serialization.copy(MergeIllegal.operator());
  }

  public void testCollisionFail() {
    AssertFail.of(() -> MergeIllegal.operator().apply(null, null));
  }
}
