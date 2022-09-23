// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.jet.DateTimeScalar;
import ch.alpine.tensor.qty.Quantity;

class ClipIntervalTest {
  @Test
  void testEqualsInterval() {
    assertEquals(Clips.interval(3, 7), Clips.interval(3, 7));
    assertFalse(Clips.interval(3, 7).equals(Clips.interval(3, 8)));
  }

  @Test
  void testEqualsQuantity() {
    Clip c1 = Clips.interval(3, 7);
    Clip c2 = Clips.interval(Quantity.of(2, "m"), Quantity.of(3, "m"));
    assertFalse(c1.equals(c2));
    assertFalse(c2.equals(c1));
    assertFalse(c1.equals(null));
    assertFalse(c2.equals(null));
    assertTrue(c1.equals(c1));
    assertTrue(c2.equals(c2));
  }

  @Test
  void testEqualsPoint() {
    assertEquals(Clips.interval(7, 7), Clips.interval(7, 7));
    assertFalse(Clips.interval(7, 7).equals(Clips.interval(7, 8)));
  }

  @Test
  void testHash() {
    assertEquals(Clips.interval(7, 7).hashCode(), Clips.interval(7, 7).hashCode());
    assertEquals(Clips.interval(3, 7).hashCode(), Clips.interval(3, 7).hashCode());
    assertNotEquals(Clips.interval(3, 7).hashCode(), Clips.interval(3, 8).hashCode());
  }

  @Test
  void testToString() {
    String string = Clips.interval(3, 7).toString();
    assertEquals(string, "Clip[3, 7]");
  }

  @Test
  void testDateTime() {
    DateTimeScalar dt1 = DateTimeScalar.of(LocalDateTime.of(2020, 12, 20, 4, 30));
    DateTimeScalar dt2 = DateTimeScalar.of(LocalDateTime.of(2020, 12, 21, 4, 30));
    Clip clip = Clips.interval(dt1, dt2);
    assertEquals(clip.width(), Quantity.of(86400, "s"));
  }

  @Test
  void testUnits0() {
    assertThrows(Throw.class, () -> Clips.interval(RealScalar.of(0), Quantity.of(0, "m")));
    assertThrows(Throw.class, () -> Clips.interval(Quantity.of(0, "m"), RealScalar.of(0)));
  }

  @Test
  void testUnits1() {
    assertThrows(Throw.class, () -> Clips.interval(RealScalar.of(0), Quantity.of(2, "m")));
    assertThrows(Throw.class, () -> Clips.interval(Quantity.of(0, "m"), RealScalar.of(2)));
    assertThrows(Throw.class, () -> Clips.interval(Quantity.of(-2, "m"), RealScalar.of(0)));
  }

  @Test
  void testVisibility() {
    assertEquals(ClipInterval.class.getModifiers(), 0);
  }
}
