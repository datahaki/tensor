// code by jph
package ch.alpine.tensor.itp;

import junit.framework.TestCase;

public class AbstractInterpolationTest extends TestCase {
  public void testVisibility() {
    assertEquals(AbstractInterpolation.class.getModifiers() & 1, 1);
  }
}
