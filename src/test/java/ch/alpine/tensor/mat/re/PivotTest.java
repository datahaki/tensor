// code by jph
package ch.alpine.tensor.mat.re;

import java.lang.reflect.Modifier;

import junit.framework.TestCase;

public class PivotTest extends TestCase {
  public void testPackageVisibility() {
    assertTrue(Modifier.isPublic(Pivot.class.getModifiers()));
  }
}
