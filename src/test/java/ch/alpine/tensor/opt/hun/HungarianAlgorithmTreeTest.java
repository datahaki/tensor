// code by jph
package ch.alpine.tensor.opt.hun;

import java.lang.reflect.Modifier;

import junit.framework.TestCase;

public class HungarianAlgorithmTreeTest extends TestCase {
  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(HungarianAlgorithmTree.class.getModifiers()));
  }
}
