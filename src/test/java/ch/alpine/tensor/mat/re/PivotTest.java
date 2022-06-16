// code by jph
package ch.alpine.tensor.mat.re;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

class PivotTest {
  @Test
  void testPackageVisibility() {
    assertTrue(Modifier.isPublic(Pivot.class.getModifiers()));
  }
}
