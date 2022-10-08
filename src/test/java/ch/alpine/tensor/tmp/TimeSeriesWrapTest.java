// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NavigableSet;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Clips;

class TimeSeriesWrapTest {
  @Test
  void testEmpty() {
    TimeSeries timeSeries = TimeSeries.wrap(new TreeSet<>(), s -> null, ResamplingMethods.LINEAR_INTERPOLATION);
    assertEquals(timeSeries.toString(), "TimeSeries[LinearInterpolation, null, 0]");
  }

  @Test
  void testSome() {
    Tensor database = RandomVariate.of(NormalDistribution.standard(), 10);
    NavigableSet<Scalar> keys = new TreeSet<>();
    Range.of(0, database.length()).stream() //
        .map(Scalar.class::cast) //
        .forEach(keys::add);
    TimeSeries timeSeries = TimeSeries.wrap( //
        keys, s -> database.get(s.number().intValue()), //
        ResamplingMethods.LINEAR_INTERPOLATION);
    assertEquals(timeSeries.domain(), Clips.interval(0, 9));
    RandomVariate.of(UniformDistribution.of(timeSeries.domain()), 20).stream() //
        .map(Scalar.class::cast).forEach(timeSeries::evaluate);
    assertTrue(timeSeries.toString().startsWith("TimeSeries["));
    assertEquals(timeSeries.resamplingMethod(), ResamplingMethods.LINEAR_INTERPOLATION);
    assertFalse(timeSeries.isEmpty());
    assertEquals(timeSeries.size(), database.length());
    Tensor path = timeSeries.path();
    assertEquals(path.get(Tensor.ALL, 0), Range.of(0, database.length()));
    assertEquals(path.get(Tensor.ALL, 1), database);
    assertSame(timeSeries, timeSeries.unmodifiable());
    TimeSeries copy = timeSeries.copy();
    assertNotSame(copy, copy.unmodifiable());
    assertThrows(Exception.class, () -> timeSeries.insert(RealScalar.of(30), Pi.VALUE));
    assertTrue(timeSeries.containsKey(RealScalar.of(3)));
    assertFalse(timeSeries.containsKey(RealScalar.of(30)));
    assertEquals(timeSeries.keySet(Clips.interval(0, 5), false).size(), 5);
    assertEquals(timeSeries.keySet(Clips.interval(0, 5), true).size(), 6);
  }
}
