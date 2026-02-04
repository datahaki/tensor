// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

class TsPredicateTest {
  @Test
  void testEquals() {
    TimeSeries ts1 = TimeSeries.path(Tensors.fromString("{{1,0}}"), ResamplingMethod.HOLD_VALUE_FROM_LEFT);
    TimeSeries ts2 = TimeSeries.path(Tensors.fromString("{{2,0}}"), ResamplingMethod.HOLD_VALUE_FROM_LEFT);
    assertFalse(TsPredicate.equals(ts1, ts2));
  }

  @Test
  void testEqualsUnmodif() {
    TimeSeries ts1 = TimeSeries.path(Tensors.fromString("{{1,0}}"), ResamplingMethod.HOLD_VALUE_FROM_LEFT);
    assertTrue(TsPredicate.equals(ts1, ts1.unmodifiable()));
  }

  @Test
  void testPredicate() {
    TimeSeries timeSeries = TimeSeries.wrap(new TreeSet<>(), _ -> null, ResamplingMethod.LINEAR_INTERPOLATION);
    assertTrue(TsPredicate.isUnmodifiable(timeSeries));
  }

  @Test
  void testRegion1() {
    TimeSeries timeSeries = TimeSeries.path(Tensors.fromString("{{1,1},{2,0},{3,1},{4,1},{5,0},{6,1}}"), ResamplingMethod.LINEAR_INTERPOLATION);
    List<Clip> list = TsPredicate.regions(timeSeries, Scalars::nonZero);
    assertTrue(list.contains(Clips.interval(1, 2)));
    assertTrue(list.contains(Clips.interval(3, 5)));
    assertTrue(list.contains(Clips.interval(6, 6)));
    assertEquals(list.size(), 3);
  }

  @Test
  void testRegion2() {
    TimeSeries timeSeries = TimeSeries.path(Tensors.fromString("{{1,0},{2,1},{3,1},{4,0},{5,1},{6,0}}"), ResamplingMethod.LINEAR_INTERPOLATION);
    List<Clip> list = TsPredicate.regions(timeSeries, Scalars::nonZero);
    assertTrue(list.contains(Clips.interval(2, 4)));
    assertTrue(list.contains(Clips.interval(5, 6)));
    assertEquals(list.size(), 2);
  }
}
