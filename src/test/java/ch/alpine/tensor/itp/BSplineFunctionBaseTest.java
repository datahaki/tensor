// code by jph
package ch.alpine.tensor.itp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BSplineFunctionBaseTest {
  @Test
  public void testVisibility() {
    assertEquals(BSplineFunctionBase.class.getModifiers() & 1, 1);
  }
}
