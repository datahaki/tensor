// code by jph
package ch.ethz.idsc.tensor.opt.nd;

import java.lang.reflect.Modifier;

import junit.framework.TestCase;

public class NdClusterTest extends TestCase {
  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(NdCluster.class.getModifiers()));
  }
}
