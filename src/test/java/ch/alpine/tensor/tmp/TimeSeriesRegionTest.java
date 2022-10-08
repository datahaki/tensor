// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

class TimeSeriesRegionTest {
  @Test
  void testRegion1() {
    TimeSeries timeSeries = TimeSeries.path(Tensors.fromString("{{1,1},{2,0},{3,1},{4,1},{5,0},{6,1}}"), ResamplingMethods.LINEAR);
    List<Clip> list = TimeSeriesRegion.of(timeSeries, Scalars::nonZero);
    assertTrue(list.contains(Clips.interval(1, 2)));
    assertTrue(list.contains(Clips.interval(3, 5)));
    assertTrue(list.contains(Clips.interval(6, 6)));
    assertEquals(list.size(), 3);
  }

  @Test
  void testRegion2() {
    TimeSeries timeSeries = TimeSeries.path(Tensors.fromString("{{1,0},{2,1},{3,1},{4,0},{5,1},{6,0}}"), ResamplingMethods.LINEAR);
    List<Clip> list = TimeSeriesRegion.of(timeSeries, Scalars::nonZero);
    assertTrue(list.contains(Clips.interval(2, 4)));
    assertTrue(list.contains(Clips.interval(5, 6)));
    assertEquals(list.size(), 2);
  }
}
