// code by jph
package ch.alpine.tensor.mat.sv;

import java.lang.reflect.Modifier;

import junit.framework.TestCase;

public class InitTest extends TestCase {
  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(Init.class.getModifiers()));
  }
}
