package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class TsReduceTest {
  @ParameterizedTest
  @EnumSource
  void testMinMax(ResamplingMethods resamplingMethods) {
    Tensor tensor = Tensors.fromString("{{1,3},{2,3},{3,3},{4,4},{5,4},{6,2},{7,2}}");
    TimeSeries timeSeries = TimeSeries.path(tensor, resamplingMethods);
    assertEquals(TsReduce.max(timeSeries).orElseThrow(), RealScalar.of(4));
    assertEquals(TsReduce.min(timeSeries).orElseThrow(), RealScalar.of(2));
  }

  @ParameterizedTest
  @EnumSource
  void testMinMaxEmpty(ResamplingMethods resamplingMethods) {
    TimeSeries timeSeries = TimeSeries.empty(resamplingMethods);
    assertFalse(TsReduce.max(timeSeries).isPresent());
    assertFalse(TsReduce.min(timeSeries).isPresent());
  }

  @ParameterizedTest
  @EnumSource
  void testEquals(ResamplingMethods resamplingMethods) {
    Tensor tensor = Tensors.fromString("{{1,3},{2,3},{3,3},{4,4},{5,4},{6,2},{7,2}}");
    TimeSeries timeSeries1 = TimeSeries.path(tensor, resamplingMethods);
    TimeSeries timeSeries2 = TimeSeries.path(tensor, resamplingMethods);
    assertEquals(timeSeries1, timeSeries2);
  }

  @ParameterizedTest
  @EnumSource
  void testNotEquals(ResamplingMethods resamplingMethods) {
    TimeSeries timeSeries1 = TimeSeries.path(Tensors.fromString("{{1,3},{2,3},{3,3},{4,4},{5,4},{6,2},{7,2}}"), resamplingMethods);
    TimeSeries timeSeries2 = TimeSeries.path(Tensors.fromString("{{1,3},{2,3},{3,3},{4,4},{5,4},{6,2},{7,3}}"), resamplingMethods);
    assertNotEquals(timeSeries1, timeSeries2);
  }
}
