// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;

class SquareWaveTest {
  @Test
  void test() {
    assertEquals(SquareWave.FUNCTION.apply(RealScalar.of(0)), RealScalar.ONE);
    assertEquals(SquareWave.FUNCTION.apply(RealScalar.of(0.5)), RealScalar.ONE.negate());
    assertEquals(SquareWave.FUNCTION.apply(RealScalar.of(1)), RealScalar.ONE);
    assertEquals(SquareWave.FUNCTION.apply(RealScalar.of(1.5)), RealScalar.ONE.negate());
  }
}
