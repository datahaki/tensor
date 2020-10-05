// code by jph
package ch.ethz.idsc.tensor;

import java.lang.reflect.Modifier;

import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testStringLastIndex() {
    String string = "{ { } } ";
    assertEquals(string.lastIndexOf(Tensor.CLOSING_BRACKET, 2), -1);
  }

  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(StaticHelper.class.getModifiers()));
  }
}
