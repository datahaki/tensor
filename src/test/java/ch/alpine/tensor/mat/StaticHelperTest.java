// code by jph
package ch.alpine.tensor.mat;

import java.lang.reflect.Modifier;

import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(StaticHelper.class.getModifiers()));
  }
}
