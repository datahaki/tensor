// code by jph
package ch.ethz.idsc.tensor.ext;

import ch.ethz.idsc.tensor.usr.AssertFail;
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
}
