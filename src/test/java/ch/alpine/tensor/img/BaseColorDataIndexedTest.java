// code by jph
package ch.alpine.tensor.img;

import junit.framework.TestCase;

public class BaseColorDataIndexedTest extends TestCase {
  public void testVisibility() {
    assertEquals(BaseColorDataIndexed.class.getModifiers() & 1, 0);
  }
}
