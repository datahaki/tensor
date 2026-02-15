// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class IntegersTest {
  @Test
  void testPositive() {
    for (int value : new int[] { 1, 2, Integer.MAX_VALUE })
      Integers.requirePositive(value);
  }

  @Test
  void testPositiveOrZero() {
    for (int value : new int[] { 0, 1, 2, Integer.MAX_VALUE })
      Integers.requirePositiveOrZero(value);
  }

  @Test
  void testPositiveFail() {
    for (int value : new int[] { Integer.MIN_VALUE, -3, -1, 0 })
      assertThrows(IllegalArgumentException.class, () -> Integers.requirePositive(value));
  }

  @Test
  void testPositiveOrZeroFail() {
    for (int value : new int[] { Integer.MIN_VALUE, -3, -1 })
      assertThrows(IllegalArgumentException.class, () -> Integers.requirePositiveOrZero(value));
  }

  @Test
  void testRequireLessThan() {
    Integers.requireLessThan(2, 3);
    assertThrows(IllegalArgumentException.class, () -> Integers.requireLessThan(3, 3));
  }

  @Test
  void testRequireLessEquals() {
    Integers.requireLessEquals(3, 3);
    assertThrows(IllegalArgumentException.class, () -> Integers.requireLessEquals(4, 3));
    try {
      Integers.requireLessEquals(4, 3);
      fail();
    } catch (Exception e) {
      assertEquals(e.getMessage(), "4 > 3");
    }
  }

  @ParameterizedTest
  @ValueSource(ints = { -2, 0, 2, Integer.MIN_VALUE })
  void testEven(int value) {
    assertTrue(Integers.isEven(value));
    assertFalse(Integers.isOdd(value));
    Integers.requireEven(value);
    assertThrows(Exception.class, () -> Integers.requireOdd(value));
  }

  @ParameterizedTest
  @ValueSource(ints = { -3, -1, 1, 3, Integer.MAX_VALUE })
  void testOdd(int value) {
    assertFalse(Integers.isEven(value));
    assertTrue(Integers.isOdd(value));
    Integers.requireOdd(value);
    assertThrows(Exception.class, () -> Integers.requireEven(value));
  }

  @Test
  void testCuriosity() {
    int value = -1;
    assertEquals(value & 1, 1);
  }

  @Test
  void testPowerOf2() {
    assertTrue(Integers.isPowerOf2(1));
    assertTrue(Integers.isPowerOf2(2));
    assertTrue(Integers.isPowerOf2(4));
    assertTrue(Integers.isPowerOf2(1 << 29));
    assertTrue(Integers.isPowerOf2(1 << 30));
    assertEquals(Integers.requirePowerOf2(4), 4);
  }

  @Test
  void testRequirePowerOf2() {
    assertThrows(Exception.class, () -> Integers.requirePowerOf2(0));
    assertThrows(Exception.class, () -> Integers.requirePowerOf2(3));
  }

  @Test
  void testPowerOf2False() {
    assertFalse(Integers.isPowerOf2(-3));
    assertFalse(Integers.isPowerOf2(-2));
    assertFalse(Integers.isPowerOf2(-1));
    assertFalse(Integers.isPowerOf2(0));
    assertFalse(Integers.isPowerOf2(3));
    assertFalse(Integers.isPowerOf2(5));
    assertFalse(Integers.isPowerOf2(6));
    assertFalse(Integers.isPowerOf2(7));
    assertFalse(Integers.isPowerOf2(Integer.MAX_VALUE));
    assertFalse(Integers.isPowerOf2(Integer.MIN_VALUE));
  }

  @Test
  void testMisra3_3() {
    int a = -5;
    int b = 3;
    int div = a / b;
    int rem = a % b;
    assertEquals(div, -1);
    assertEquals(rem, -2);
  }

  @Test
  void testMisra6_10_3_int() {
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
  void testMisra6_10_3_short() {
    short a = Short.MAX_VALUE;
    short b = Short.MAX_VALUE;
    int c1 = a + b;
    assertEquals(c1, 65534);
  }

  @Test
  void testMisra6_10_3_byte() {
    byte a = Byte.MAX_VALUE;
    byte b = Byte.MAX_VALUE;
    int c1 = a + b;
    assertEquals(c1, 254);
  }

  @Test
  void testRequireEquals() {
    assertEquals(3, Integers.requireEquals(3, 3));
    assertEquals(7, Integers.requireEquals(7, 7));
    assertThrows(IllegalArgumentException.class, () -> Integers.requireEquals(3, 4));
    assertThrows(IllegalArgumentException.class, () -> Integers.requireEquals(3, -3));
  }

  @Test
  void testRequireEqualsMessage() {
    assertThrows(Exception.class, () -> Integers.requireEquals(3, 4));
  }

  @Test
  void testIsPermutation() {
    assertTrue(Integers.isPermutation(new int[] {}));
    assertTrue(Integers.isPermutation(new int[] { 2, 0, 1 }));
    assertTrue(Integers.isPermutation(new int[] { 2, 3, 1, 0 }));
    assertFalse(Integers.isPermutation(new int[] { 2, 3, 1 }));
    assertFalse(Integers.isPermutation(new int[] { 0, 2 }));
    assertFalse(Integers.isPermutation(new int[] { -1, 0 }));
  }

  @Test
  void testRequirePermutation() {
    Integers.requirePermutation(new int[] { 0, 2, 1 });
    assertThrows(IllegalArgumentException.class, () -> Integers.requirePermutation(new int[] { 2, 3 }));
  }

  @Test
  void testRequirePermutationMessage() {
    assertThrows(Exception.class, () -> Integers.requirePermutation(new int[] { 0, 2 }));
  }

  @Test
  void testRequirePermutationLong() {
    assertThrows(Exception.class, () -> Integers.requirePermutation(new int[20]));
  }

  @Test
  void testClip() {
    assertEquals(Integer.MIN_VALUE, Integers.clip(Integer.MIN_VALUE, Integer.MAX_VALUE).applyAsInt(Integer.MIN_VALUE));
    assertEquals(Integer.MAX_VALUE, Integers.clip(0, Integer.MAX_VALUE).applyAsInt(Integer.MAX_VALUE));
    assertEquals(3, Integers.clip(Integer.MIN_VALUE, Integer.MAX_VALUE).applyAsInt(3));
    assertEquals(3, Integers.clip(0, 5).applyAsInt(3));
    assertEquals(4, Integers.clip(4, 4).applyAsInt(3));
    assertEquals(4, Integers.clip(4, 4).applyAsInt(4));
    assertEquals(4, Integers.clip(4, 4).applyAsInt(5));
  }

  @Test
  void testClipFail() {
    assertThrows(IllegalArgumentException.class, () -> Integers.clip(0, -1));
    assertThrows(IllegalArgumentException.class, () -> Integers.clip(3, 2));
  }

  @Test
  void testParity() {
    assertEquals(Integers.parity(new int[] { 0, 1 }), 0);
    assertEquals(Integers.parity(new int[] { 1, 0 }), 1);
    assertEquals(Integers.parity(new int[] { 2, 0, 1 }), 0);
    assertEquals(Integers.parity(new int[] { 2, 1, 0 }), 1);
  }

  @Test
  void testParityFail() {
    assertThrows(IllegalArgumentException.class, () -> Integers.parity(new int[] { 0, 0 }));
    assertThrows(IllegalArgumentException.class, () -> Integers.parity(new int[] { 1, 1 }));
    assertThrows(IllegalArgumentException.class, () -> Integers.parity(new int[] { 2, 1 }));
  }

  @Test
  void testAsList() {
    assertEquals(Integers.asList(new int[] { 3, 4, 556 }), Arrays.asList(3, 4, 556));
    // assertEquals(Integers.asList(3, 4, 556), Arrays.asList(3, 4, 556));
  }

  @Test
  void testAsListNullFail() {
    assertThrows(NullPointerException.class, () -> Integers.asList(null));
  }

  @Test
  void testLog2Floor() {
    assertEquals(Integers.log2Floor(1), 0);
    assertEquals(Integers.log2Floor(2), 1);
    assertEquals(Integers.log2Floor(3), 1);
    assertEquals(Integers.log2Floor(4), 2);
    assertEquals(Integers.log2Floor(5), 2);
    assertEquals(Integers.log2Floor(1024 + 123), 10);
  }

  @Test
  void testLog2Exact() {
    assertEquals(Integers.log2Exact(1), 0);
    assertEquals(Integers.log2Exact(2), 1);
    assertEquals(Integers.log2Exact(4), 2);
    assertThrows(Exception.class, () -> Integers.log2Exact(0));
    assertThrows(Exception.class, () -> Integers.log2Exact(3));
  }

  @Test
  void testLog2Ceil() {
    assertEquals(Integers.log2Ceiling(1), 0);
    assertEquals(Integers.log2Ceiling(2), 1);
    assertEquals(Integers.log2Ceiling(3), 2);
    assertEquals(Integers.log2Ceiling(4), 2);
    assertEquals(Integers.log2Ceiling(5), 3);
    assertEquals(Integers.log2Ceiling(1024 + 123), 11);
  }

  @Test
  void testFailZero() {
    assertThrows(Exception.class, () -> Integers.log2Ceiling(0));
  }

  @Test
  void testFailNegative() {
    assertThrows(Exception.class, () -> Integers.log2Ceiling(-1));
  }
}
