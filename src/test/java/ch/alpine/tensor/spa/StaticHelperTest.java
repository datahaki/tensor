// code by jph
package ch.alpine.tensor.spa;

import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testVisibility() {
    assertEquals(StaticHelper.class.getModifiers() & 1, 0);
  }
}
