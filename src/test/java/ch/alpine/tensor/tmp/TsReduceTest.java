// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;

class TsReduceTest {
  @ParameterizedTest
  @EnumSource
  void testMinMax(ResamplingMethods resamplingMethods) {
    ResamplingMethod resamplingMethod = resamplingMethods.get();
    Tensor tensor = Tensors.fromString("{{1,3},{2,3},{3,3},{4,4},{5,4},{6,2},{7,2}}");
    TimeSeries timeSeries = TimeSeries.path(tensor, resamplingMethod);
    assertEquals(TsReduce.max(timeSeries).orElseThrow(), RealScalar.of(4));
    assertEquals(TsReduce.min(timeSeries).orElseThrow(), RealScalar.of(2));
  }

  @ParameterizedTest
  @EnumSource
  void testMinMaxEmpty(ResamplingMethods resamplingMethods) {
    ResamplingMethod resamplingMethod = resamplingMethods.get();
    TimeSeries timeSeries = TimeSeries.empty(resamplingMethod);
    assertFalse(TsReduce.max(timeSeries).isPresent());
    assertFalse(TsReduce.min(timeSeries).isPresent());
  }

  @ParameterizedTest
  @EnumSource
  void testEquals(ResamplingMethods resamplingMethods) {
    ResamplingMethod resamplingMethod = resamplingMethods.get();
    Tensor tensor = Tensors.fromString("{{1,3},{2,3},{3,3},{4,4},{5,4},{6,2},{7,2}}");
    TimeSeries timeSeries1 = TimeSeries.path(tensor, resamplingMethod);
    TimeSeries timeSeries2 = TimeSeries.path(tensor, resamplingMethod);
    assertTrue(TsPredicate.equals(timeSeries1, timeSeries2));
  }

  @ParameterizedTest
  @EnumSource
  void testNotEquals(ResamplingMethods resamplingMethods) {
    ResamplingMethod resamplingMethod = resamplingMethods.get();
    TimeSeries timeSeries1 = TimeSeries.path(Tensors.fromString("{{1,3},{2,3},{3,3},{4,4},{5,4},{6,2},{7,2}}"), resamplingMethod);
    TimeSeries timeSeries2 = TimeSeries.path(Tensors.fromString("{{1,3},{2,3},{3,3},{4,4},{5,4},{6,2},{7,3}}"), resamplingMethod);
    assertFalse(TsPredicate.equals(timeSeries1, timeSeries2));
  }

  @Test
  void testMultidim() {
    int n = 50;
    Tensor matrix = RandomVariate.of(DiscreteUniformDistribution.of(3, 5), n, 4);
    matrix.set(Range.of(0, n), Tensor.ALL, 0);
    TimeSeries timeSeries = TimeSeries.table(matrix.stream(), ResamplingMethod.HOLD_VALUE_FROM_LEFT);
    Optional<Tensor> min = TsReduce.min(timeSeries);
    assertEquals(min.orElseThrow(), Tensors.vector(3, 3, 3));
    Optional<Tensor> max = TsReduce.max(timeSeries);
    assertEquals(max.orElseThrow(), Tensors.vector(4, 4, 4));
  }

  @Test
  void testFirstLast() {
    Tensor p1 = Tensors.fromString("{{1, 3}, {4, 0}, {5, 6}, {7, 0}, {8, 6}, {10, 2}}");
    TimeSeries timeSeries = TimeSeries.path(p1, ResamplingMethod.LINEAR_INTERPOLATION);
    Optional<Scalar> value1 = TsReduce.firstValue(timeSeries);
    assertEquals(value1.orElseThrow(), RealScalar.of(3));
    Optional<Scalar> value2 = TsReduce.lastValue(timeSeries);
    assertEquals(value2.orElseThrow(), RealScalar.of(2));
  }

  @Test
  void testFirstLastEmpty() {
    TimeSeries timeSeries = TimeSeries.empty(ResamplingMethod.LINEAR_INTERPOLATION);
    assertFalse(TsReduce.firstValue(timeSeries).isPresent());
    assertFalse(TsReduce.lastValue(timeSeries).isPresent());
  }

  @Test
  void testTable() {
    Tensor tensor = ResourceData.of("/ch/alpine/tensor/io/dateobject.csv");
    TimeSeries timeSeries = TimeSeries.table(tensor.stream(), ResamplingMethod.HOLD_VALUE_FROM_LEFT);
    Optional<Tensor> value = TsReduce.lastValue(timeSeries);
    assertEquals(value.orElseThrow(), Tensors.fromString("{2398749, 2233.2[m]}"));
  }
}
