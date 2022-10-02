// code by jph
package ch.alpine.tensor.prc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

class TimeSeriesOpTest {
  @Test
  void test() {
    Tensor p1 = Tensors.fromString("{{1, 3}, {4, 3}, {5, 6}, {7, 5}, {10, 2}}");
    TimeSeries ts1 = TimeSeries.of(p1);
    assertEquals(ts1.path(), p1);
    TimeSeries ts2 = TimeSeries.of(Tensors.fromString("{{2, 1}, {3, 2}, {6, 3}, {8, 2}, {10, 4}, {11, 3}}"));
    Clip clip = Clips.intersection(ts1.support(), ts2.support());
    assertEquals(clip, Clips.interval(2, 10));
    // System.out.println(clip);
    TimeSeries timeSeries = TimeSeriesOp.add(ts1, ts2);
    assertEquals(timeSeries.path(), Tensors.fromString( // consistent with mathematica:
        "{{2, 4}, {3, 5}, {4, 16/3}, {5, 26/3}, {6, 17/2}, {7, 15/2}, {8, 6}, {10, 6}}"));
  }
}
