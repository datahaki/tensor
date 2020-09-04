// code by jph
package ch.ethz.idsc.tensor.mat;

import java.lang.reflect.Modifier;

import junit.framework.TestCase;

public class PivotsTest extends TestCase {
  public void testPackageVisibility() {
    assertTrue(Modifier.isPublic(Pivots.class.getModifiers()));
    assertTrue(Modifier.isPublic(Pivot.class.getModifiers()));
    assertTrue(Modifier.isPublic(Pivots.class.getModifiers()));
  }
}
