// code by jph
package ch.alpine.tensor.mat.ex;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;

class StaticHelperTest {
  @Test
  void testExponents() {
    assertEquals(StaticHelper.exponent(RealScalar.of(0)), 1);
    assertEquals(StaticHelper.exponent(RealScalar.of(0.99)), 2);
    assertEquals(StaticHelper.exponent(RealScalar.of(1)), 2);
    assertEquals(StaticHelper.exponent(RealScalar.of(1.01)), 4);
  }
}
