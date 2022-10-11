// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensors;

class TsPredicateTest {
  @Test
  void testEquals() {
    TimeSeries ts1 = TimeSeries.path(Tensors.fromString("{{1,0}}"), ResamplingMethods.HOLD_VALUE_FROM_LEFT);
    TimeSeries ts2 = TimeSeries.path(Tensors.fromString("{{2,0}}"), ResamplingMethods.HOLD_VALUE_FROM_LEFT);
    assertFalse(TsPredicate.equals(ts1, ts2));
  }

  @Test
  void testEqualsUnmodif() {
    TimeSeries ts1 = TimeSeries.path(Tensors.fromString("{{1,0}}"), ResamplingMethods.HOLD_VALUE_FROM_LEFT);
    assertTrue(TsPredicate.equals(ts1, ts1.unmodifiable()));
  }

  @Test
  void testPredicate() {
    TimeSeries timeSeries = TimeSeries.wrap(new TreeSet<>(), s -> null, ResamplingMethods.LINEAR_INTERPOLATION);
    assertTrue(TsPredicate.isUnmodifiable(timeSeries));
  }
}
