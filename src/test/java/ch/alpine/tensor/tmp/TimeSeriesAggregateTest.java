// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.qty.DateTime;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Entrywise;

class TimeSeriesAggregateTest {
  @Test
  void test() {
    TimeSeries timeSeries = TimeSeries.empty(ResamplingMethod.LINEAR_INTERPOLATION);
    assertEquals(timeSeries.size(), 0);
    assertTrue(timeSeries.isEmpty());
    DateTime dateTime = DateTime.of(1993, 4, 5, 4, 5);
    timeSeries.insert(dateTime, RealScalar.of(3));
    timeSeries.insert(dateTime.add(Quantity.of(3, "h")), RealScalar.of(2));
    timeSeries.insert(dateTime.add(Quantity.of(3.2, "h")), RealScalar.of(5));
    timeSeries.insert(dateTime.add(Quantity.of(24, "h")), RealScalar.of(1));
    TimeSeriesAggregate timeSeriesAggregate = //
        TimeSeriesAggregate.of(Entrywise.max(), ResamplingMethod.HOLD_VALUE_FROM_LEFT);
    TimeSeries result = timeSeriesAggregate.of(timeSeries, dateTime.subtract(Quantity.of(1, "min")), Quantity.of(5, "h"));
    assertEquals(result.path().length(), 2);
  }
}
