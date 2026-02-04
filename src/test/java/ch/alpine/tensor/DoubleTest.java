// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DoubleTest {
  public static long countSteps(double start, double end) {
    // Handle cases where the numbers are the same
    if (Double.compare(start, end) == 0)
      return 0;
    // Convert to bit patterns
    long startBits = Double.doubleToRawLongBits(start);
    long endBits = Double.doubleToRawLongBits(end);
    // Adjust for negative numbers
    // In IEEE 754, the most significant bit is the sign bit.
    // We convert to a "lexicographical" long where values increase monotonically.
    startBits = adjustSign(startBits);
    endBits = adjustSign(endBits);
    return Math.abs(endBits - startBits);
  }

  private static long adjustSign(long bits) {
    // If negative, flip the bits to align with the positive scale
    if (bits < 0) {
      return 0x8000000000000000L - bits;
    }
    return bits;
  }

  @Test
  void testZeros() {
    final double d1 = 0.0;
    final double d2 = -0.0;
    assertFalse(Double.valueOf(d1).equals(d2));
    assertFalse(d1 < d2);
    assertFalse(d2 < d1);
    assertTrue(d1 <= d2);
    assertTrue(d2 <= d1);
    assertTrue(d1 >= d2);
    assertTrue(d2 >= d1);
    // the tensor library overrides this logic (see DoubleScalar)
    assertEquals(Double.compare(d1, d2), +1);
    assertEquals(Double.compare(d2, d1), -1);
  }

  @Test
  void testReciprocal() {
    double e1 = 1e-300;
    double e2 = 1e-200;
    double l2 = 1e200;
    double r1 = e2 / e1;
    double rb = e2 * (1.0 / e1);
    double r2 = l2 / e1;
    assertTrue(Double.isFinite(r1));
    assertTrue(Double.isFinite(rb));
    assertFalse(Double.isFinite(r2));
  }

  @Test
  void testNextAfter() {
    long countSteps = countSteps(2.0, Math.nextDown(2.0));
    IO.println(countSteps);
  }
}
