// code by jph
package ch.alpine.tensor.opt.hun;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

class HungarianAlgorithmGraphTest {
  @Test
  void testPackageVisibility() {
    assertFalse(Modifier.isPublic(HungarianAlgorithmGraph.class.getModifiers()));
  }
}
