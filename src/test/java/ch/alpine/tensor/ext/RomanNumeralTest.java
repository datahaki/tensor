// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RomanNumeralTest {
  @Test
  void test() {
    assertEquals(RomanNumeral.of(0), "");
    assertEquals(RomanNumeral.of(49), "XLIX");
    assertEquals(RomanNumeral.of(50), "L");
  }
}
