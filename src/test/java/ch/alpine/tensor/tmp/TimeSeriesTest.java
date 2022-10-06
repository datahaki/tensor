// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.sca.Clips;

class TimeSeriesTest {
  @Test
  void testMultiInsertion() {
    TimeSeries timeSeries = TimeSeries.empty(ResamplingMethods.HOLD_HI);
    timeSeries.insert(RealScalar.ONE, Tensors.empty());
    timeSeries.insert(RealScalar.ONE, Tensors.empty());
    timeSeries.insert(RealScalar.ONE, Tensors.empty());
    assertEquals(timeSeries.size(), 1);
  }

  @Test
  void testPack() {
    AtomicInteger atomicInteger = new AtomicInteger();
    TimeSeries timeSeries = TimeSeries.of(Stream.generate(() -> new TsEntry( //
        RealScalar.of(atomicInteger.getAndIncrement()), //
        Tensors.vector(1, 2, 3))).limit(11), //
        ResamplingMethods.HOLD_LO_SPARSE);
    assertEquals(timeSeries.path(), Tensors.fromString("{{0, {1, 2, 3}}, {10, {1, 2, 3}}}"));
    assertTrue(timeSeries.keySet(Clips.interval(30, 40), false).isEmpty());
    assertTrue(timeSeries.keySet(Clips.interval(30, 40), true).isEmpty());
  }

  @Test
  void testHoldLoPack1() {
    Tensor tensor = Tensors.fromString("{{1,3},{2,3},{3,3},{4,4},{5,4},{6,2},{7,2}}");
    Tensor path = TimeSeries.path(tensor, ResamplingMethods.HOLD_LO_SPARSE).path();
    assertEquals(path, Tensors.fromString("{{1, 3}, {4, 4}, {6, 2}, {7, 2}}"));
  }

  @Test
  void testHoldLoPack2() {
    Tensor tensor = Tensors.fromString("{{1,3},{2,4},{3,3},{4,4},{5,4},{6,2},{7,2},{8,3}}");
    Tensor path = TimeSeries.path(tensor, ResamplingMethods.HOLD_LO_SPARSE).path();
    assertEquals(path, Tensors.fromString("{{1, 3},{2,4},{3,3}, {4, 4}, {6, 2}, {8, 3}}"));
  }

  @Test
  void testNoPack() {
    AtomicInteger atomicInteger = new AtomicInteger();
    TimeSeries timeSeries = TimeSeries.of(Stream.generate(() -> new TsEntry( //
        RealScalar.of(atomicInteger.getAndIncrement()), //
        Tensors.vector(1, 2, 3))).limit(11), //
        ResamplingMethods.LINEAR_INTERPOLATION);
    assertEquals(timeSeries.size(), 11);
  }

  @Test
  void testPath() {
    Tensor p1 = Tensors.fromString("{{1, {1,1}}, {4, {3,2}}}");
    TimeSeries timeSeries = TimeSeries.path(p1, ResamplingMethods.LINEAR_INTERPOLATION);
    Tensor path = timeSeries.path();
    path.set(r -> r.append(RealScalar.ZERO), 0);
    // System.out.println(path);
    // System.out.println(timeSeries.path());
  }

  @Test
  void testDimension() {
    Tensor p1 = Tensors.fromString("{{1, {1,1}}, {4, {3,2}}}").unmodifiable();
    TimeSeries timeSeries = TimeSeries.path(p1, ResamplingMethods.LINEAR_INTERPOLATION);
    Tensor path = timeSeries.path();
    path.set(r -> r.append(RealScalar.ZERO), 0, 1);
    assertEquals(timeSeries.path(), p1);
  }

  @Test
  @Disabled // TODO TENSOR EASY reenable
  void testTable() {
    Tensor tensor = ResourceData.of("/ch/alpine/tensor/io/dateobject.csv");
    TimeSeries timeSeries = TimeSeries.table(tensor.stream(), ResamplingMethods.HOLD_LO);
    Tensor value = TimeSeriesOp.lastValue(timeSeries);
    assertEquals(value, Tensors.fromString("{2398749, 2233.2[m]}"));
  }

  @Test
  void testFails() {
    Tensor p1 = Tensors.fromString("{{1, {1,1}}, {4, {3,2}, 3}}").unmodifiable();
    assertThrows(Exception.class, () -> TimeSeries.path(p1, ResamplingMethods.LINEAR_INTERPOLATION));
  }

  @Test
  void testNullFails() {
    assertThrows(Exception.class, () -> TimeSeries.path((Tensor) null, ResamplingMethods.LINEAR_INTERPOLATION));
    assertThrows(Exception.class, () -> TimeSeries.empty(null));
    Tensor p1 = Tensors.fromString("{{1, {1,1}}, {4, {3,2}}}").unmodifiable();
    TimeSeries.path(p1, ResamplingMethods.LINEAR_INTERPOLATION);
    assertThrows(Exception.class, () -> TimeSeries.path(p1.stream(), null));
  }
}
