// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

class StaticHelperTest {
  @Test
  void testPackage() {
    assertFalse(Modifier.isPublic(StaticHelper.class.getModifiers()));
  }
}
