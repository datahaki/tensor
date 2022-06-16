// code by jph
package ch.alpine.tensor.sca.win;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Chop;

class BlackmanWindowTest {
  @Test
  void testSimple() {
    Scalar result = BlackmanWindow.FUNCTION.apply(RealScalar.of(0.2));
    Scalar expect = RealScalar.of(0.50978713763747791812); // checked with Mathematica
    Chop._12.requireClose(result, expect);
  }

  @Test
  void testFail() {
    assertEquals(BlackmanWindow.FUNCTION.apply(RealScalar.of(-0.51)), RealScalar.ZERO);
  }
}
