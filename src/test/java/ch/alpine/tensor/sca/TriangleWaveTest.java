// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;

class TriangleWaveTest {
  @Test
  void test() {
    assertEquals(TriangleWave.FUNCTION.apply(RealScalar.of(0)), RealScalar.of(0));
    assertEquals(TriangleWave.FUNCTION.apply(RealScalar.of(0.1)), RealScalar.of(0.4));
    assertEquals(TriangleWave.FUNCTION.apply(RealScalar.of(0.25)), RealScalar.of(1.0));
    assertEquals(TriangleWave.FUNCTION.apply(RealScalar.of(0.5)), RealScalar.of(0));
    assertEquals(TriangleWave.FUNCTION.apply(RealScalar.of(0.75)), RealScalar.of(-1));
  }
}
