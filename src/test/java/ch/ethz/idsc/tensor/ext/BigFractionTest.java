// code by jph
package ch.ethz.idsc.tensor.ext;

import java.lang.reflect.Modifier;

import ch.ethz.idsc.tensor.usr.AssertFail;
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
    assertEquals(BigFraction.of(7, 3).hashCode(), 1181);
    assertEquals(BigFraction.of(3, 7).hashCode(), 1061);
  }

  public void testEquals() {
    assertFalse(BigFraction.of(7, 3).equals(null));
    assertFalse(BigFraction.of(7, 3).equals("abc"));
    assertTrue(BigFraction.of(7, 3).equals(BigFraction.of(14, 6)));
    assertTrue(BigFraction.of(-1, 3).equals(BigFraction.of(1, -3)));
  }

  public void testDenZero() {
    AssertFail.of(() -> BigFraction.of(3, 0));
  }

  public void testPackageVisibility() {
    assertTrue(Modifier.isPublic(BigFraction.class.getModifiers()));
  }
}
