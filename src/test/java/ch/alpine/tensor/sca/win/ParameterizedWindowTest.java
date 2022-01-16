// code by jph
package ch.alpine.tensor.sca.win;

import java.lang.reflect.Modifier;

import junit.framework.TestCase;

public class ParameterizedWindowTest extends TestCase {
  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(ParameterizedWindow.class.getModifiers()));
  }
}
