// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

class TimeSeriesWindowTest {
  @Test
  void testCut() {
    Tensor tensor = ResourceData.of("/ch/alpine/tensor/io/dateobject.csv");
    TimeSeries timeSeries = //
        TimeSeries.table(tensor.stream(), ResamplingMethod.HOLD_VALUE_FROM_LEFT).unmodifiable();
    Clip clip = Clips.interval(Scalars.fromString("2022-01-11T10:30"), Scalars.fromString("2022-03-03T12:30"));
    TimeSeries ts1 = TimeSeriesWindow.of(timeSeries, clip);
    assertTrue(ts1.keySet(clip, true).contains(clip.min()));
    assertTrue(ts1.keySet(clip, true).contains(clip.max()));
    assertFalse(ts1.keySet(clip, false).contains(clip.max()));
    assertEquals(ts1.path().length(), 3);
  }

  @Test
  void testSimple1() {
    TimeSeries ts1 = TimeSeries.path( //
        Tensors.fromString("{{1, 0}, {4, 3}, {5, 6}, {7, 5}, {10, 2}}"), //
        ResamplingMethod.LINEAR_INTERPOLATION).unmodifiable();
    Clip clip = Clips.interval(2, 9);
    TimeSeries timeSeries = TimeSeriesWindow.of(ts1, clip);
    assertEquals(timeSeries.path(), Tensors.fromString("{{2, 1}, {4, 3}, {5, 6}, {7, 5}, {9, 3}}"));
  }

  @Test
  void testSimple2() {
    TimeSeries ts1 = TimeSeries.path( //
        Tensors.fromString("{{1, 0}, {4, 3}, {5, 6}, {7, 5}, {10, 2}}"), //
        ResamplingMethod.LINEAR_INTERPOLATION).unmodifiable();
    Clip clip = Clips.interval(2, 7);
    TimeSeries timeSeries = TimeSeriesWindow.of(ts1, clip);
    assertEquals(timeSeries.path(), Tensors.fromString("{{2, 1}, {4, 3}, {5, 6}, {7, 5}}"));
  }

  @Test
  void testSimple3() {
    TimeSeries ts1 = TimeSeries.path( //
        Tensors.fromString("{{1, 0}, {4, 3}, {5, 6}, {7, 5}, {10, 2}}"), //
        ResamplingMethod.LINEAR_INTERPOLATION).unmodifiable();
    Clip clip = Clips.interval(4, 7);
    TimeSeries timeSeries = TimeSeriesWindow.of(ts1, clip);
    assertEquals(timeSeries.path(), Tensors.fromString("{{4, 3}, {5, 6}, {7, 5}}"));
  }
}
