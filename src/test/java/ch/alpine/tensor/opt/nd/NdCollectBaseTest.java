// code by jph
package ch.alpine.tensor.opt.nd;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

class NdCollectBaseTest {
  @Test
  void testVisibility() {
    assertTrue(Modifier.isPublic(NdCollectBase.class.getModifiers()));
  }
}
