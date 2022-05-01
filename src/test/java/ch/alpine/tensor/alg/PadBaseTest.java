// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

class PadBaseTest {
  @Test
  public void testSimple() {
    assertFalse(Modifier.isPublic(PadBase.class.getModifiers()));
  }
}
