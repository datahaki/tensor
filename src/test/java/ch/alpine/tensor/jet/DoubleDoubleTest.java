package ch.alpine.tensor.jet;

import org.junit.jupiter.api.Test;

class DoubleDoubleTest {
  static void assertNormalized(DoubleDouble x) {
    double hi = x.hi();
    double lo = x.lo();
    // lo must be tiny relative to hi (non-overlapping)
    if (Math.abs(lo) > Math.ulp(hi)) {
      throw new AssertionError("Not normalized: " + x);
    }
  }

  @Test
  void testRandom() {
    DoubleDouble extendedDouble = new DoubleDouble(.2, .3);
    assertNormalized(extendedDouble);
  }

  @Test
  void testSimple() {
    DoubleDouble doubleDouble = DoubleDouble.of(1e-10);
    IO.println(doubleDouble.toString());
  }
}
