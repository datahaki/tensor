// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.qty.DateTime;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

class TimeSeriesRescaleTest {
  @Test
  void testTimeSeries() {
    TimeSeries timeSeries = TimeSeries.empty(ResamplingMethods.LINEAR_INTERPOLATION);
    assertEquals(timeSeries.size(), 0);
    assertTrue(timeSeries.isEmpty());
    DateTime dateTime = DateTime.of(1993, 4, 5, 4, 5);
    assertThrows(Exception.class, () -> timeSeries.insert(dateTime, null));
    timeSeries.insert(dateTime, Tensors.vector(1, 2, 3));
    timeSeries.insert(dateTime.add(Quantity.of(3, "h")), Tensors.vector(2, 3, 0));
    timeSeries.insert(dateTime.add(Quantity.of(4, "h")), Tensors.vector(2, 3, 0));
    timeSeries.insert(dateTime.add(Quantity.of(24, "h")), Tensors.vector(0, 0, 6));
    Clip clip = Clips.interval(3, 4);
    TimeSeries result = TimeSeriesRescale.of(timeSeries, clip);
    String string = "{{3, {1, 2, 3}}, {25/8, {2, 3, 0}}, {19/6, {2, 3, 0}}, {4, {0, 0, 6}}}";
    Tensor tensor = Tensors.fromString(string);
    assertEquals(result.path(), tensor);
    assertTrue(ExactTensorQ.of(tensor));
    assertEquals(result.domain(), clip);
  }
}
