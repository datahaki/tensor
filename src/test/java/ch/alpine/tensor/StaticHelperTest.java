// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

class StaticHelperTest {
  @Test
  public void testStringLastIndex() {
    String string = "{ { } } ";
    assertEquals(string.lastIndexOf(Tensor.CLOSING_BRACKET, 2), -1);
  }

  @Test
  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(StaticHelper.class.getModifiers()));
  }
}
