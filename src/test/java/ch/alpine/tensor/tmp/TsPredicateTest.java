// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.TreeSet;

import org.junit.jupiter.api.Test;

class TsPredicateTest {
  @Test
  void testPredicate() {
    TimeSeries timeSeries = TimeSeries.wrap(new TreeSet<>(), s -> null, ResamplingMethods.LINEAR);
    assertTrue(TsPredicate.isUnmodifiable(timeSeries));
  }
}
