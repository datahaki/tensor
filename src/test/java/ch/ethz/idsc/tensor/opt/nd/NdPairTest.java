// code by jph
package ch.ethz.idsc.tensor.opt.nd;

import java.io.Serializable;
import java.lang.reflect.Modifier;

import junit.framework.TestCase;

public class NdPairTest extends TestCase {
  public void testSerializable() {
    assertFalse(NdPair.class.isInstance(Serializable.class));
  }

  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(NdPair.class.getModifiers()));
  }
}
