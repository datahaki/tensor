// code by jph
package ch.alpine.tensor.sca.win;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

class ParameterizedWindowTest {
  @Test
  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(ParameterizedWindow.class.getModifiers()));
  }
}
