// code by jph
package ch.alpine.tensor.itp;

import junit.framework.TestCase;

public class BSplineFunctionBaseTest extends TestCase {
  public void testVisibility() {
    assertEquals(BSplineFunctionBase.class.getModifiers() & 1, 1);
  }
}
