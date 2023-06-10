// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;

class TsOpTest {
  @Test
  void testIndicator() {
    Tensor p1 = Tensors.fromString("{{1, 3}, {4, 0}, {5, 6}, {7, 0}, {8, 6}, {10, 2}}");
    TimeSeries ts1 = TimeSeries.path(p1, ResamplingMethod.LINEAR_INTERPOLATION);
    TimeSeries timeSeries = TsOp.indicator(ts1, Sign::isPositive);
    Tensor tensor = TimeSeriesIntegrate.of(timeSeries, timeSeries.domain());
    assertEquals(tensor, RealScalar.of(3 + 2 + 2));
    assertTrue(ExactTensorQ.of(tensor));
  }

  @Test
  void testIndicator2() {
    TreeSet<Scalar> treeSet = new TreeSet<>();
    TimeSeries ts1 = TsOp.indicator(treeSet);
    assertTrue(ts1.isEmpty());
    treeSet.add(RealScalar.of(4));
    TimeSeries ts2 = TsOp.indicator(treeSet);
    assertEquals(ts2.path(), Tensors.fromString("{{4, 0}}"));
    treeSet.add(RealScalar.of(10));
    treeSet.add(RealScalar.of(12));
    treeSet.add(RealScalar.of(20));
    TimeSeries ts3 = TsOp.indicator(treeSet);
    assertEquals(ts3.domain(), Clips.interval(4, 20));
    assertEquals(ts3.path(), Tensors.fromString("{{4, 0}, {10, 1}, {12, 2}, {20, 3}}"));
  }
}
