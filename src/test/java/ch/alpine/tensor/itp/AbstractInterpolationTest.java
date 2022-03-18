// code by jph
package ch.alpine.tensor.itp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class AbstractInterpolationTest {
  @Test
  public void testVisibility() {
    assertEquals(AbstractInterpolation.class.getModifiers() & 1, 1);
  }
}
