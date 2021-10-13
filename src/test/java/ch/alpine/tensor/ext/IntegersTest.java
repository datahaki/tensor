// code by jph
package ch.alpine.tensor.ext;

import java.util.Arrays;

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

  public void testRequireEquals() {
    assertEquals(3, Integers.requireEquals(3, 3));
    assertEquals(7, Integers.requireEquals(7, 7));
    AssertFail.of(() -> Integers.requireEquals(3, 4));
    AssertFail.of(() -> Integers.requireEquals(3, -3));
  }

  public void testRequireEqualsMessage() {
    try {
      Integers.requireEquals(3, 4);
      fail();
    } catch (Exception exception) {
      assertEquals(exception.getMessage(), "3 != 4");
    }
  }

  public void testIsPermutation() {
    assertTrue(Integers.isPermutation(new int[] {}));
    assertTrue(Integers.isPermutation(new int[] { 2, 0, 1 }));
    assertTrue(Integers.isPermutation(new int[] { 2, 3, 1, 0 }));
    assertFalse(Integers.isPermutation(new int[] { 2, 3, 1 }));
    assertFalse(Integers.isPermutation(new int[] { 0, 2 }));
    assertFalse(Integers.isPermutation(new int[] { -1, 0 }));
  }

  public void testRequirePermutation() {
    Integers.requirePermutation(new int[] { 0, 2, 1 });
    AssertFail.of(() -> Integers.requirePermutation(new int[] { 2, 3 }));
  }

  public void testRequirePermutationMessage() {
    try {
      Integers.requirePermutation(new int[] { 0, 2 });
      fail();
    } catch (Exception exception) {
      assertEquals(exception.getMessage(), "0 2");
    }
  }

  public void testParity() {
    assertEquals(Integers.parity(new int[] { 0, 1 }), 0);
    assertEquals(Integers.parity(new int[] { 1, 0 }), 1);
    assertEquals(Integers.parity(new int[] { 2, 0, 1 }), 0);
    assertEquals(Integers.parity(new int[] { 2, 1, 0 }), 1);
  }

  public void testParityFail() {
    AssertFail.of(() -> Integers.parity(new int[] { 0, 0 }));
    AssertFail.of(() -> Integers.parity(new int[] { 1, 1 }));
    AssertFail.of(() -> Integers.parity(new int[] { 2, 1 }));
  }

  public void testAsList() {
    assertEquals(Integers.asList(new int[] { 3, 4, 556 }), Arrays.asList(3, 4, 556));
    // assertEquals(Integers.asList(3, 4, 556), Arrays.asList(3, 4, 556));
  }

  public void testAsListNullFail() {
    AssertFail.of(() -> Integers.asList(null));
  }
}
