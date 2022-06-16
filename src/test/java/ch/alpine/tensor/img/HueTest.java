// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Color;

import org.junit.jupiter.api.Test;

class HueTest {
  @Test
  void testMod() {
    assertEquals(Hue.of(0, 1, 1, 1), Hue.of(1, 1, 1, 1));
    assertEquals(Hue.of(0.2, 1, 1, 1), Hue.of(3.2, 1, 1, 1));
    assertEquals(Hue.of(0.2, 1, 1, 1), Hue.of(-3.8, 1, 1, 1));
  }

  @Test
  void testAlpha() {
    assertEquals(Hue.of(0.2, 1, 1, 1).getAlpha(), 255);
    assertEquals(Hue.of(0.1, 1, 1, 0.5).getAlpha(), 128);
    assertEquals(Hue.of(0.1, 1, 1, 0).getAlpha(), 0);
  }

  @Test
  void testSaturationEps() {
    assertEquals(Hue.of(0.1, Math.nextUp(0.0), 0.2, 0), Hue.of(0.1, 0.0, 0.2, 0));
    assertEquals(Hue.of(0.1, Math.nextUp(0.0), 1, 1), Color.WHITE);
  }

  @Test
  void testFail() {
    assertThrows(IllegalArgumentException.class, () -> Hue.of(0, 0, 1.01, 0));
    assertThrows(IllegalArgumentException.class, () -> Hue.of(Double.POSITIVE_INFINITY, 1, 1, 1));
  }
}
