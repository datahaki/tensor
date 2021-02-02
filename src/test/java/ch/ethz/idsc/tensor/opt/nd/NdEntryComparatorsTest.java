// code by jph
package ch.ethz.idsc.tensor.opt.nd;

import java.lang.reflect.Modifier;

import junit.framework.TestCase;

public class NdEntryComparatorsTest extends TestCase {
  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(NdEntryComparators.class.getModifiers()));
  }
}
