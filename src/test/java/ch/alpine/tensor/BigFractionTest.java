// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

public class BigFractionTest {
  @Test
  public void testCompactString() {
    assertEquals(BigFraction.of(24, 1).toString(), "24");
    assertEquals(BigFraction.of(24, -1).toString(), "-24");
    assertEquals(BigFraction.of(24, 7).toString(), "24/7");
    assertEquals(BigFraction.of(24, -7).toString(), "-24/7");
  }

  @Test
  public void testDivide() {
    BigFraction num = BigFraction.of(1, 1);
    BigFraction den = BigFraction.of(0, 1);
    assertThrows(ArithmeticException.class, () -> num.divide(den));
  }

  @Test
  public void testHash() {
    assertFalse(BigFraction.of(7, 3).hashCode() == BigFraction.of(3, 7).hashCode());
    assertFalse(BigFraction.of(1, 2).hashCode() == BigFraction.of(1, 3).hashCode());
    assertFalse(BigFraction.of(3, 1).hashCode() == BigFraction.of(4, 1).hashCode());
  }

  @Test
  public void testEquals() {
    assertFalse(BigFraction.of(7, 3).equals(null));
    assertTrue(BigFraction.of(7, 3).equals(BigFraction.of(14, 6)));
    assertTrue(BigFraction.of(-1, 3).equals(BigFraction.of(1, -3)));
  }

  @Test
  public void testEqualsObjects() {
    Object object = BigFraction.of(7, 3);
    assertFalse(object.equals("abc"));
  }

  @Test
  public void testDenZero() {
    assertThrows(ArithmeticException.class, () -> BigFraction.of(3, 0));
  }

  @Test
  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(BigFraction.class.getModifiers()));
  }
}
