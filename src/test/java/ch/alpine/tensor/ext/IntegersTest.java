// code by jph
package ch.alpine.tensor.ext;

import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class IntegersTest extends TestCase {
  public void testPositive() {
    for (int value : new int[] { 1, 2, Integer.MAX_VALUE })
      Integers.requirePositive(value);
  }

  public void testPositiveOrZero() {
    for (int value : new int[] { 0, 1, 2, Integer.MAX_VALUE })
      Integers.requirePositiveOrZero(value);
  }

  public void testPositiveFail() {
    for (int value : new int[] { Integer.MIN_VALUE, -3, -1, 0 })
      AssertFail.of(() -> Integers.requirePositive(value));
  }

  public void testPositiveOrZeroFail() {
    for (int value : new int[] { Integer.MIN_VALUE, -3, -1 })
      AssertFail.of(() -> Integers.requirePositiveOrZero(value));
  }

  public void testIsEven() {
    assertTrue(Integers.isEven(-2));
    assertTrue(Integers.isEven(0));
    assertTrue(Integers.isEven(2));
    assertTrue(Integers.isEven(Integer.MIN_VALUE));
  }

  public void testIsEvenFalse() {
    assertFalse(Integers.isEven(-3));
    assertFalse(Integers.isEven(-1));
    assertFalse(Integers.isEven(1));
    assertFalse(Integers.isEven(3));
    assertFalse(Integers.isEven(Integer.MAX_VALUE));
  }

  public void testCuriosity() {
    int value = -1;
    assertEquals(value & 1, 1);
  }

  public void testPowerOf2() {
    assertTrue(Integers.isPowerOf2(1));
    assertTrue(Integers.isPowerOf2(2));
    assertFalse(Integers.isPowerOf2(3));
    assertTrue(Integers.isPowerOf2(4));
    assertFalse(Integers.isPowerOf2(5));
    assertFalse(Integers.isPowerOf2(6));
  }

  public void testPowerOf2Fail() {
    AssertFail.of(() -> Integers.isPowerOf2(-3));
    AssertFail.of(() -> Integers.isPowerOf2(-2));
    AssertFail.of(() -> Integers.isPowerOf2(-1));
    AssertFail.of(() -> Integers.isPowerOf2(0));
  }

  public void testMisra3_3() {
    int a = -5;
    int b = 3;
    int div = a / b;
    int rem = a % b;
    assertEquals(div, -1);
    assertEquals(rem, -2);
  }

  public void testMisra6_10_3_int() {
    int a = Integer.MAX_VALUE;
    int b = Integer.MAX_VALUE;
    long c1 = a + b;
    long c2 = ((long) a) + b;
    long c3 = a + ((long) b);
    assertEquals(c1, -2);
    assertEquals(c2, 4294967294L);
    assertEquals(c3, 4294967294L);
  }

  public void testMisra6_10_3_short() {
    short a = Short.MAX_VALUE;
    short b = Short.MAX_VALUE;
    int c1 = a + b;
    assertEquals(c1, 65534);
  }

  public void testMisra6_10_3_byte() {
    byte a = Byte.MAX_VALUE;
    byte b = Byte.MAX_VALUE;
    int c1 = a + b;
    assertEquals(c1, 254);
  }
}
