// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

class TimeSeriesOpTest {
  @Test
  void testMathematica() {
    Tensor p1 = Tensors.fromString("{{1, 3}, {4, 3}, {5, 6}, {7, 5}, {10, 2}}");
    TimeSeries ts1 = TimeSeries.path(p1, ResamplingMethods.LINEAR_INTERPOLATION);
    assertEquals(ts1.resamplingMethod(), ResamplingMethods.LINEAR_INTERPOLATION);
    assertEquals(ts1.path(), p1);
    TimeSeries ts2 = TimeSeries.path( //
        Tensors.fromString("{{2, 1}, {3, 2}, {6, 3}, {8, 2}, {10, 4}, {11, 3}}"), //
        ResamplingMethods.LINEAR_INTERPOLATION);
    Clip clip = Clips.intersection(ts1.domain(), ts2.domain());
    assertEquals(clip, Clips.interval(2, 10));
    {
      TimeSeries timeSeries = TimeSeriesOp.add(ts1, ts2);
      assertEquals(timeSeries.path(), Tensors.fromString( // consistent with mathematica:
          "{{2, 4}, {3, 5}, {4, 16/3}, {5, 26/3}, {6, 17/2}, {7, 15/2}, {8, 6}, {10, 6}}"));
    }
    {
      TimeSeries timeSeries = TimeSeriesOp.times(ts1, ts2);
      assertEquals(timeSeries.path(), Tensors.fromString( // consistent with mathematica:
          "{{2, 3}, {3, 6}, {4, 7}, {5, 16}, {6, 33/2}, {7, 25/2}, {8, 8}, {10, 8}}"));
    }
  }

  @Test
  void testExtendLo() {
    TimeSeries timeSeries = TimeSeries.empty(ResamplingMethods.HOLD_LO_SPARSE);
    timeSeries.insert(RealScalar.of(3), RealScalar.ZERO);
    timeSeries.insert(RealScalar.of(6), RealScalar.ZERO);
    timeSeries.insert(RealScalar.of(8), RealScalar.ZERO);
    assertEquals(timeSeries.size(), 2);
    TimeSeriesOp.extend(timeSeries, RealScalar.of(0));
    assertEquals(timeSeries.path(), Tensors.fromString("{{0, 0}, {3, 0}, {8, 0}}"));
  }

  @Test
  void testExtendHi() {
    TimeSeries timeSeries = TimeSeries.empty(ResamplingMethods.HOLD_LO_SPARSE);
    timeSeries.insert(RealScalar.of(3), RealScalar.ZERO);
    timeSeries.insert(RealScalar.of(6), RealScalar.ZERO);
    timeSeries.insert(RealScalar.of(8), RealScalar.ZERO);
    assertEquals(timeSeries.size(), 2);
    TimeSeriesOp.extend(timeSeries, RealScalar.of(10));
    assertEquals(timeSeries.path(), Tensors.fromString("{{3, 0}, {10, 0}}"));
  }

  @Test
  void testExtendClip() {
    TimeSeries timeSeries = TimeSeries.empty(ResamplingMethods.HOLD_LO_SPARSE);
    timeSeries.insert(RealScalar.of(3), RealScalar.ZERO);
    timeSeries.insert(RealScalar.of(6), RealScalar.ZERO);
    timeSeries.insert(RealScalar.of(8), RealScalar.ZERO);
    assertEquals(timeSeries.size(), 2);
    TimeSeriesOp.extend(timeSeries, Clips.interval(3, 7));
    assertEquals(timeSeries.path(), Tensors.fromString("{{3, 0}, {8, 0}}"));
    TimeSeriesOp.extend(timeSeries, Clips.interval(3, 9));
    assertEquals(timeSeries.path(), Tensors.fromString("{{3, 0}, {9, 0}}"));
    TimeSeriesOp.extend(timeSeries, Clips.interval(1, 10));
    assertEquals(timeSeries.path(), Tensors.fromString("{{1, 0}, {3, 0}, {10, 0}}"));
  }
}
