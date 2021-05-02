// code by jph
package ch.alpine.tensor.opt.nd;

import java.lang.reflect.Modifier;

import junit.framework.TestCase;

public class NdMatchComparatorsTest extends TestCase {
  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(NdMatchComparators.class.getModifiers()));
  }
}
