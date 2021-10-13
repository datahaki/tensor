// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ClipIntervalTest extends TestCase {
  public void testEqualsInterval() {
    assertEquals(Clips.interval(3, 7), Clips.interval(3, 7));
    assertFalse(Clips.interval(3, 7).equals(Clips.interval(3, 8)));
  }

  public void testEqualsQuantity() {
    Clip c1 = Clips.interval(3, 7);
    Clip c2 = Clips.interval(Quantity.of(2, "m"), Quantity.of(3, "m"));
    assertFalse(c1.equals(c2));
    assertFalse(c2.equals(c1));
    assertFalse(c1.equals(null));
    assertFalse(c2.equals(null));
    assertTrue(c1.equals(c1));
    assertTrue(c2.equals(c2));
  }

  public void testEqualsPoint() {
    assertEquals(Clips.interval(7, 7), Clips.interval(7, 7));
    assertFalse(Clips.interval(7, 7).equals(Clips.interval(7, 8)));
  }

  public void testHash() {
    assertEquals(Clips.interval(7, 7).hashCode(), Clips.interval(7, 7).hashCode());
    assertEquals(Clips.interval(3, 7).hashCode(), Clips.interval(3, 7).hashCode());
    assertFalse(Clips.interval(3, 7).hashCode() == Clips.interval(3, 8).hashCode());
  }

  public void testToString() {
    String string = Clips.interval(3, 7).toString();
    assertEquals(string, "Clip[3, 7]");
  }

  public void testUnits0() {
    AssertFail.of(() -> Clips.interval(RealScalar.of(0), Quantity.of(0, "m")));
    AssertFail.of(() -> Clips.interval(Quantity.of(0, "m"), RealScalar.of(0)));
  }

  public void testUnits1() {
    AssertFail.of(() -> Clips.interval(RealScalar.of(0), Quantity.of(2, "m")));
    AssertFail.of(() -> Clips.interval(Quantity.of(0, "m"), RealScalar.of(2)));
    AssertFail.of(() -> Clips.interval(Quantity.of(-2, "m"), RealScalar.of(0)));
  }

  public void testVisibility() {
    assertEquals(ClipInterval.class.getModifiers(), 0);
  }
}
