// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class TsReduceTest {
  @Test
  void testMinMax() {
    for (ResamplingMethod resamplingMethod : TestHelper.list()) {
      Tensor tensor = Tensors.fromString("{{1,3},{2,3},{3,3},{4,4},{5,4},{6,2},{7,2}}");
      TimeSeries timeSeries = TimeSeries.path(tensor, resamplingMethod);
      assertEquals(TsReduce.max(timeSeries).orElseThrow(), RealScalar.of(4));
      assertEquals(TsReduce.min(timeSeries).orElseThrow(), RealScalar.of(2));
    }
  }

  @Test
  void testMinMaxEmpty() {
    for (ResamplingMethod resamplingMethod : TestHelper.list()) {
      TimeSeries timeSeries = TimeSeries.empty(resamplingMethod);
      assertFalse(TsReduce.max(timeSeries).isPresent());
      assertFalse(TsReduce.min(timeSeries).isPresent());
    }
  }

  @Test
  void testEquals() {
    for (ResamplingMethod resamplingMethod : TestHelper.list()) {
      Tensor tensor = Tensors.fromString("{{1,3},{2,3},{3,3},{4,4},{5,4},{6,2},{7,2}}");
      TimeSeries timeSeries1 = TimeSeries.path(tensor, resamplingMethod);
      TimeSeries timeSeries2 = TimeSeries.path(tensor, resamplingMethod);
      assertTrue(TsPredicate.equals(timeSeries1, timeSeries2));
    }
  }

  @Test
  void testNotEquals() {
    for (ResamplingMethod resamplingMethod : TestHelper.list()) {
      TimeSeries timeSeries1 = TimeSeries.path(Tensors.fromString("{{1,3},{2,3},{3,3},{4,4},{5,4},{6,2},{7,2}}"), resamplingMethod);
      TimeSeries timeSeries2 = TimeSeries.path(Tensors.fromString("{{1,3},{2,3},{3,3},{4,4},{5,4},{6,2},{7,3}}"), resamplingMethod);
      assertFalse(TsPredicate.equals(timeSeries1, timeSeries2));
    }
  }
}
