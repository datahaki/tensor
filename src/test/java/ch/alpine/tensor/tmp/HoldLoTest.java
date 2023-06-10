// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;

class HoldLoTest {
  @Test
  void testDirac() {
    TimeSeries timeSeries = TimeSeries.empty(ResamplingMethod.HOLD_VALUE_FROM_LEFT);
    timeSeries.insert(DoubleScalar.NEGATIVE_INFINITY, RealScalar.ZERO);
    timeSeries.insert(RealScalar.TWO, RealScalar.ONE);
    timeSeries.insert(DoubleScalar.POSITIVE_INFINITY, RealScalar.ONE);
    assertEquals(timeSeries.domain().width(), DoubleScalar.POSITIVE_INFINITY);
    assertEquals(timeSeries.evaluate(DoubleScalar.NEGATIVE_INFINITY), RealScalar.ZERO);
    assertEquals(timeSeries.evaluate(RealScalar.TWO), RealScalar.ONE);
    assertEquals(timeSeries.evaluate(DoubleScalar.POSITIVE_INFINITY), RealScalar.ONE);
    assertEquals(timeSeries.evaluate(RealScalar.of(-10)), RealScalar.ZERO);
    assertEquals(timeSeries.evaluate(RealScalar.of(10)), RealScalar.ONE);
  }
}
