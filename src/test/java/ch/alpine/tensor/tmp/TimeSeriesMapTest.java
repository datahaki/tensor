// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class TimeSeriesMapTest {
  @Test
  void testMultiply() {
    Tensor p1 = Tensors.fromString("{{1, 3}, {4, 3}, {5, 6}, {7, 5}, {10, 2}}");
    TimeSeries ts1 = TimeSeries.path(p1, ResamplingMethod.HOLD_VALUE_FROM_LEFT);
    TimeSeries ts2 = TimeSeriesMap.of(RealScalar.TWO::multiply, ResamplingMethod.HOLD_VALUE_FROM_RIGHT).apply(ts1);
    assertFalse(TsPredicate.equals(ts1, ts2));
  }

  @Test
  void testMultiplySparse() {
    Tensor p1 = Tensors.fromString("{{1, 3}, {4, 3}, {5, 6}, {7, 5}, {10, 2}}");
    TimeSeries ts1 = TimeSeries.path(p1, ResamplingMethod.HOLD_VALUE_FROM_LEFT_SPARSE);
    TimeSeries ts2 = TimeSeriesMap.of(RealScalar.ZERO::multiply).apply(ts1);
    assertFalse(TsPredicate.equals(ts1, ts2));
    assertEquals(ts2.path(), Tensors.fromString("{{1, 0}, {10, 0}}"));
  }
}
