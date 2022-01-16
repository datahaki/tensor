// code by jph
package ch.alpine.tensor.sca;

import java.lang.reflect.Modifier;

import junit.framework.TestCase;

public class NDoubleTest extends TestCase {
  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(NDouble.class.getModifiers()));
  }
}
