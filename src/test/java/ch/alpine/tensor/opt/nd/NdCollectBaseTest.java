// code by jph
package ch.alpine.tensor.opt.nd;

import junit.framework.TestCase;

public class NdCollectBaseTest extends TestCase {
  public void testVisibility() {
    assertEquals(NdCollectBase.class.getModifiers() & 1, 1);
  }
}
