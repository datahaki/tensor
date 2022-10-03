// code by jph
package ch.alpine.tensor.prc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class TimeSeriesTest {
  @Test
  void testPath() {
    Tensor p1 = Tensors.fromString("{{1, {1,1}}, {4, {3,2}}}");
    TimeSeries timeSeries = TimeSeries.of(p1);
    Tensor path = timeSeries.path();
    path.set(r -> r.append(RealScalar.ZERO), 0);
    // System.out.println(path);
    // System.out.println(timeSeries.path());
  }

  @Test
  void testDimension() {
    Tensor p1 = Tensors.fromString("{{1, {1,1}}, {4, {3,2}}}").unmodifiable();
    TimeSeries timeSeries = TimeSeries.of(p1);
    Tensor path = timeSeries.path();
    path.set(r -> r.append(RealScalar.ZERO), 0, 1);
    assertEquals(timeSeries.path(), p1);
  }

  @Test
  void testFails() {
    Tensor p1 = Tensors.fromString("{{1, {1,1}}, {4, {3,2}, 3}}").unmodifiable();
    assertThrows(Exception.class, () -> TimeSeries.of(p1));
  }

  @Test
  void testNullFails() {
    assertThrows(Exception.class, () -> TimeSeries.of((Tensor) null));
    assertThrows(Exception.class, () -> TimeSeries.empty(null));
    Tensor p1 = Tensors.fromString("{{1, {1,1}}, {4, {3,2}}}").unmodifiable();
    TimeSeries.of(p1);
    assertThrows(Exception.class, () -> TimeSeries.of(p1.stream(), null));
  }
}
