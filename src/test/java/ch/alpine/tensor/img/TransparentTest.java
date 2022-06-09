// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.alg.Array;

class TransparentTest {
  @Test
  public void testSimple() {
    assertEquals(Transparent.rgba(), Array.zeros(4));
    Transparent.rgba().set(RealScalar.ONE, 3);
    assertEquals(Transparent.rgba(), Array.zeros(4));
  }

  @Test
  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(Transparent.class.getModifiers()));
  }
}
