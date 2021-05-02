// code by jph
package ch.alpine.tensor.ext;

import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class StringRepeatTest extends TestCase {
  public void testSimple() {
    assertEquals(StringRepeat.of("ab", 3), "ababab");
  }

  public void testFailNegative() {
    assertEquals(StringRepeat.of("ab", 0), "");
    AssertFail.of(() -> StringRepeat.of("ab", -1));
  }
}
