// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class IntegersTest {
  @Test
  public void testPositive() {
    for (int value : new int[] { 1, 2, Integer.MAX_VALUE })
      Integers.requirePositive(value);
  }

  @Test
  public void testPositiveOrZero() {
    for (int value : new int[] { 0, 1, 2, Integer.MAX_VALUE })
      Integers.requirePositiveOrZero(value);
  }

  @Test
  public void testPositiveFail() {
    for (int value : new int[] { Integer.MIN_VALUE, -3, -1, 0 })
      assertThrows(IllegalArgumentException.class, () -> Integers.requirePositive(value));
  }

  @Test
  public void testPositiveOrZeroFail() {
    for (int value : new int[] { Integer.MIN_VALUE, -3, -1 })
      assertThrows(IllegalArgumentException.class, () -> Integers.requirePositiveOrZero(value));
  }

  @Test
  public void testRequireLessThan() {
    Integers.requireLessThan(2, 3);
    assertThrows(IllegalArgumentException.class, () -> Integers.requireLessThan(3, 3));
  }

  @Test
  public void testRequireLessEquals() {
    Integers.requireLessEquals(3, 3);
    assertThrows(IllegalArgumentException.class, () -> Integers.requireLessEquals(4, 3));
  }

  @Test
  public void testIsEven() {
    assertTrue(Integers.isEven(-2));
    assertTrue(Integers.isEven(0));
    assertTrue(Integers.isEven(2));
    assertTrue(Integers.isEven(Integer.MIN_VALUE));
  }

  @Test
  public void testIsEvenFalse() {
    assertFalse(Integers.isEven(-3));
    assertFalse(Integers.isEven(-1));
    assertFalse(Integers.isEven(1));
    assertFalse(Integers.isEven(3));
    assertFalse(Integers.isEven(Integer.MAX_VALUE));
  }

  @Test
  public void testCuriosity() {
    int value = -1;
    assertEquals(value & 1, 1);
  }

  @Test
  public void testPowerOf2() {
    assertTrue(Integers.isPowerOf2(1));
    assertTrue(Integers.isPowerOf2(2));
    assertFalse(Integers.isPowerOf2(3));
    assertTrue(Integers.isPowerOf2(4));
    assertFalse(Integers.isPowerOf2(5));
    assertFalse(Integers.isPowerOf2(6));
  }

  @Test
  public void testPowerOf2Fail() {
    assertThrows(IllegalArgumentException.class, () -> Integers.isPowerOf2(-3));
    assertThrows(IllegalArgumentException.class, () -> Integers.isPowerOf2(-2));
    assertThrows(IllegalArgumentException.class, () -> Integers.isPowerOf2(-1));
    assertThrows(IllegalArgumentException.class, () -> Integers.isPowerOf2(0));
  }

  @Test
  public void testMisra3_3() {
    int a = -5;
    int b = 3;
    int div = a / b;
    int rem = a % b;
    assertEquals(div, -1);
    assertEquals(rem, -2);
  }

  @Test
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

  @Test
  public void testMisra6_10_3_short() {
    short a = Short.MAX_VALUE;
    short b = Short.MAX_VALUE;
    int c1 = a + b;
    assertEquals(c1, 65534);
  }

  @Test
  public void testMisra6_10_3_byte() {
    byte a = Byte.MAX_VALUE;
    byte b = Byte.MAX_VALUE;
    int c1 = a + b;
    assertEquals(c1, 254);
  }

  @Test
  public void testRequireEquals() {
    assertEquals(3, Integers.requireEquals(3, 3));
    assertEquals(7, Integers.requireEquals(7, 7));
    assertThrows(IllegalArgumentException.class, () -> Integers.requireEquals(3, 4));
    assertThrows(IllegalArgumentException.class, () -> Integers.requireEquals(3, -3));
  }

  @Test
  public void testRequireEqualsMessage() {
    assertThrows(Exception.class, () -> Integers.requireEquals(3, 4));
  }

  @Test
  public void testIsPermutation() {
    assertTrue(Integers.isPermutation(new int[] {}));
    assertTrue(Integers.isPermutation(new int[] { 2, 0, 1 }));
    assertTrue(Integers.isPermutation(new int[] { 2, 3, 1, 0 }));
    assertFalse(Integers.isPermutation(new int[] { 2, 3, 1 }));
    assertFalse(Integers.isPermutation(new int[] { 0, 2 }));
    assertFalse(Integers.isPermutation(new int[] { -1, 0 }));
  }

  @Test
  public void testRequirePermutation() {
    Integers.requirePermutation(new int[] { 0, 2, 1 });
    assertThrows(IllegalArgumentException.class, () -> Integers.requirePermutation(new int[] { 2, 3 }));
  }

  @Test
  public void testRequirePermutationMessage() {
    assertThrows(Exception.class, () -> Integers.requirePermutation(new int[] { 0, 2 }));
  }

  @Test
  public void testRequirePermutationLong() {
    assertThrows(Exception.class, () -> Integers.requirePermutation(new int[20]));
  }

  @Test
  public void testParity() {
    assertEquals(Integers.parity(new int[] { 0, 1 }), 0);
    assertEquals(Integers.parity(new int[] { 1, 0 }), 1);
    assertEquals(Integers.parity(new int[] { 2, 0, 1 }), 0);
    assertEquals(Integers.parity(new int[] { 2, 1, 0 }), 1);
  }

  @Test
  public void testParityFail() {
    assertThrows(IllegalArgumentException.class, () -> Integers.parity(new int[] { 0, 0 }));
    assertThrows(IllegalArgumentException.class, () -> Integers.parity(new int[] { 1, 1 }));
    assertThrows(IllegalArgumentException.class, () -> Integers.parity(new int[] { 2, 1 }));
  }

  @Test
  public void testAsList() {
    assertEquals(Integers.asList(new int[] { 3, 4, 556 }), Arrays.asList(3, 4, 556));
    // assertEquals(Integers.asList(3, 4, 556), Arrays.asList(3, 4, 556));
  }

  @Test
  public void testAsListNullFail() {
    assertThrows(NullPointerException.class, () -> Integers.asList(null));
  }
}
