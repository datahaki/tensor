// code by jph
package ch.alpine.tensor.opt.nd;

import java.lang.reflect.Modifier;

import junit.framework.TestCase;

public class NdClusterTest extends TestCase {
  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(BoundedNdCluster.class.getModifiers()));
  }
}
