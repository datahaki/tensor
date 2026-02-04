// code by jph
package ch.alpine.tensor.mat.sv;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

class InitTest {
  @Test
  void testPackageVisibility() {
    assertFalse(Modifier.isPublic(Init.class.getModifiers()));
  }
}
