// code by jph
package ch.alpine.tensor.opt.lp;

import java.lang.reflect.Modifier;

import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testSimple() {
    assertTrue(StaticHelper.isInsideRange(Tensors.vector(3, 2, 1), 4));
    assertFalse(StaticHelper.isInsideRange(Tensors.vector(1, 3, 0), 3));
  }

  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(StaticHelper.class.getModifiers()));
  }
}
