// code by jph
package ch.alpine.tensor.opt.hun;

import java.lang.reflect.Modifier;

import junit.framework.TestCase;

public class BipartitionImplTest extends TestCase {
  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(BipartitionImpl.class.getModifiers()));
  }
}
