// code by jph
package ch.alpine.tensor;

import java.lang.reflect.Modifier;

import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class BigFractionTest extends TestCase {
  public void testCompactString() {
    assertEquals(BigFraction.of(24, 1).toString(), "24");
    assertEquals(BigFraction.of(24, -1).toString(), "-24");
    assertEquals(BigFraction.of(24, 7).toString(), "24/7");
    assertEquals(BigFraction.of(24, -7).toString(), "-24/7");
  }

  public void testDivide() {
    BigFraction num = BigFraction.of(1, 1);
    BigFraction den = BigFraction.of(0, 1);
    AssertFail.of(() -> num.divide(den));
  }

  public void testHash() {
    assertFalse(BigFraction.of(7, 3).hashCode() == BigFraction.of(3, 7).hashCode());
    assertFalse(BigFraction.of(1, 2).hashCode() == BigFraction.of(1, 3).hashCode());
    assertFalse(BigFraction.of(3, 1).hashCode() == BigFraction.of(4, 1).hashCode());
  }

  public void testEquals() {
    assertFalse(BigFraction.of(7, 3).equals(null));
    assertTrue(BigFraction.of(7, 3).equals(BigFraction.of(14, 6)));
    assertTrue(BigFraction.of(-1, 3).equals(BigFraction.of(1, -3)));
  }

  public void testEqualsObjects() {
    Object object = BigFraction.of(7, 3);
    assertFalse(object.equals("abc"));
  }

  public void testDenZero() {
    AssertFail.of(() -> BigFraction.of(3, 0));
  }

  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(BigFraction.class.getModifiers()));
  }
}
