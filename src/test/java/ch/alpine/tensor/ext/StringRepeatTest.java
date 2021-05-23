// code by jph
package ch.alpine.tensor.ext;

import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class StringRepeatTest extends TestCase {
  public void testSimple() {
    assertEquals("ab".repeat(3), "ababab");
  }

  public void testFailNegative() {
    assertEquals("ab".repeat(0), "");
    AssertFail.of(() -> "ab".repeat(-1));
  }
}
