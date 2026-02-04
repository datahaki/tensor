package ch.alpine.tensor.opt.fnd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;

class GoldenRatioTest {
  @Test
  void test() {
    assertEquals(GoldenRatio.VALUE.reciprocal().add(RealScalar.ONE), GoldenRatio.VALUE);
  }
}
