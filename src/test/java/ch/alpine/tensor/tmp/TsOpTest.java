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
    TimeSeries ts1 = TimeSeries.path(p1, ResamplingMethods.LINEAR_INTERPOLATION);
    TimeSeries timeSeries = TsOp.indicator(ts1, Sign::isPositive);
    Tensor tensor = TimeSeriesIntegrate.of(timeSeries, timeSeries.domain());
    assertEquals(tensor, RealScalar.of(3 + 2 + 2));
    assertTrue(ExactTensorQ.of(tensor));
    assertEquals(TsOp.firstValue(ts1), RealScalar.of(3));
  }

  @Test
  void testIndicator2() {
    TreeSet<Scalar> treeSet = new TreeSet<>();
    treeSet.add(RealScalar.of(4));
    treeSet.add(RealScalar.of(10));
    treeSet.add(RealScalar.of(12));
    treeSet.add(RealScalar.of(20));
    TimeSeries timeSeries = TsOp.indicator(treeSet);
    assertEquals(timeSeries.domain(), Clips.interval(4, 20));
  }
}
