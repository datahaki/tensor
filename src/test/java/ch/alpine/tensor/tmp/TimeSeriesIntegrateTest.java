// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Clips;

class TimeSeriesIntegrateTest {
  @Test
  void testIntegrateBlocks() {
    Tensor p1 = Tensors.fromString("{{1, 3}, {4, 3}, {5, 6}, {7, 5}, {10, 2}}");
    TimeSeries ts1 = TimeSeries.path(p1, ResamplingMethods.HOLD_VALUE_FROM_LEFT);
    assertEquals(ts1.resamplingMethod(), ResamplingMethods.HOLD_VALUE_FROM_LEFT);
    assertThrows(Exception.class, () -> TimeSeriesIntegrate.of(ts1, Clips.interval(0, 3)));
    assertThrows(Exception.class, () -> TimeSeriesIntegrate.of(ts1, Clips.interval(2, 11)));
    assertThrows(Exception.class, () -> TimeSeriesIntegrate.of(ts1, Clips.interval(0, 11)));
    Tensor value1 = TimeSeriesIntegrate.of(ts1, ts1.domain());
    assertEquals(value1, RealScalar.of(3 * 3 + 3 + 2 * 6 + 3 * 5));
    Tensor value2 = TimeSeriesIntegrate.of(ts1, Clips.interval(2, 8));
    assertEquals(value2, RealScalar.of(2 * 3 + 3 + 2 * 6 + 1 * 5));
    TimeSeries integrate = TimeSeriesIntegrate.of(ts1);
    assertEquals(integrate.domain(), ts1.domain());
    assertEquals(ts1.keySet(ts1.domain(), true), integrate.keySet(ts1.domain(), true));
    assertEquals(integrate.evaluate(RealScalar.of(10)), RealScalar.of(39));
  }

  @Test
  void testIntegrateLinear() {
    Tensor p1 = Tensors.fromString("{{1, 3}, {4, 3}, {5, 6}, {7, 5}, {10, 2}}");
    TimeSeries ts1 = TimeSeries.path(p1, ResamplingMethods.LINEAR_INTERPOLATION);
    Tensor value1 = TimeSeriesIntegrate.of(ts1, ts1.domain());
    Tolerance.CHOP.requireClose(value1, RealScalar.of(3 * 3 + 4.5 + 2 * 5.5 + 3 * 3.5));
    Tensor value2 = TimeSeriesIntegrate.of(ts1, Clips.interval(2, 8));
    Tensor eval = ts1.evaluate(RealScalar.of(7.5));
    assertEquals(eval, RealScalar.of(4.5));
    Tolerance.CHOP.requireClose(value2, RealScalar.of(2 * 3 + 4.5 + 2 * 5.5 + 1 * 4.5));
  }

  @Test
  void testIntegrateEmpty() {
    for (ResamplingMethod resamplingMethod : TestHelper.list()) {
      TimeSeries timeSeries = TimeSeries.empty(resamplingMethod);
      TimeSeries result = TimeSeriesIntegrate.of(timeSeries);
      assertTrue(result.isEmpty());
    }
  }

  @Test
  void testException() {
    for (ResamplingMethod resamplingMethod : TestHelper.list()) {
      TimeSeries timeSeries = TimeSeries.empty(resamplingMethod);
      assertThrows(Throw.class, () -> TimeSeriesIntegrate.of(timeSeries, Clips.interval(0, 1)));
    }
  }
}
