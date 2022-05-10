// code by jph
package ch.alpine.tensor.opt.nd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NdCollectBaseTest {
  @Test
  public void testVisibility() {
    assertEquals(NdCollectBase.class.getModifiers() & 1, 1);
  }
}
