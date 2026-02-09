// code by jph
package ch.alpine.tensor.itp;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

class AbstractInterpolationTest {
  @Test
  void testVisibility() {
    assertTrue(Modifier.isPublic(AbstractInterpolation.class.getModifiers()));
  }
}
