// code by jph
package ch.alpine.tensor.opt.nd;

import java.io.Serializable;

import junit.framework.TestCase;

public class NdBoundsTest extends TestCase {
  public void testNonSerializable() {
    assertFalse(NdBounds.class.isInstance(Serializable.class));
  }

  public void testPackageVisibility() {
    // assertFalse(Modifier.isPublic(NdBounds.class.getModifiers()));
  }
}
