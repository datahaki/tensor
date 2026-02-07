// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

class StaticHelperTest {
  @Test
  void testPublic() {
    assertFalse(Modifier.isPublic(StaticHelper.class.getModifiers()));
  }
}
