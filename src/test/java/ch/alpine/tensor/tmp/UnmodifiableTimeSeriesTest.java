// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;

class UnmodifiableTimeSeriesTest {
  @Test
  void test() {
    TimeSeries timeSeries = TimeSeries.empty(ResamplingMethods.HOLD_LO_SPARSE);
    timeSeries.insert(RealScalar.of(3), Tensors.vector(1, 2, 3));
    timeSeries.insert(RealScalar.of(4), Tensors.vector(2, 3, 4));
    timeSeries.insert(RealScalar.of(5), Tensors.vector(5, 2, 3));
    timeSeries.insert(RealScalar.of(7), Tensors.vector(5, 2, 3));
    assertThrows(Exception.class, () -> timeSeries.insert(RealScalar.of(7), Tensors.vector(5, 2, 3, 4)));
    TimeSeries ts1 = timeSeries.unmodifiable();
    assertSame(ts1, ts1.unmodifiable());
    assertThrows(Exception.class, () -> ts1.insert(RealScalar.of(6), Tensors.vector(5, 2, 3)));
    // ts1.
  }
}
