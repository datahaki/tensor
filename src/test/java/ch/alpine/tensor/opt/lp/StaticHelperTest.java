// code by jph
package ch.alpine.tensor.opt.lp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensors;

class StaticHelperTest {
  @Test
  void testSimple() {
    assertTrue(StaticHelper.isInsideRange(Tensors.vector(3, 2, 1), 4));
    assertFalse(StaticHelper.isInsideRange(Tensors.vector(1, 3, 0), 3));
  }

  @Test
  void testPackageVisibility() {
    assertFalse(Modifier.isPublic(StaticHelper.class.getModifiers()));
  }
}
