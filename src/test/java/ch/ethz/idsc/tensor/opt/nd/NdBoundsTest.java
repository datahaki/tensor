// code by jph
package ch.ethz.idsc.tensor.opt.nd;

import java.io.Serializable;
import java.lang.reflect.Modifier;

import junit.framework.TestCase;

public class NdBoundsTest extends TestCase {
  public void testNonSerializable() {
    assertFalse(NdBounds.class.isInstance(Serializable.class));
  }

  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(NdBounds.class.getModifiers()));
  }
}
