// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

class NDoubleTest {
  @Test
  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(NDouble.class.getModifiers()));
  }
}
