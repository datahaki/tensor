package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.qty.DateTime;

class MeanSummaryStatisticsTest {
  @Test
  void test() {
    MeanSummaryStatistics meanSummaryStatistics = new MeanSummaryStatistics();
    meanSummaryStatistics.accept(DateTime.of(1234, 11, 3, 12, 23));
    meanSummaryStatistics.accept(DateTime.of(1834, 10, 3, 12, 23));
    Tensor mean = meanSummaryStatistics.getMean().orElseThrow();
    assertInstanceOf(DateTime.class, mean);
  }

  @Test
  void testCombine() {
    MeanSummaryStatistics mss1 = new MeanSummaryStatistics();
    MeanSummaryStatistics mss2 = new MeanSummaryStatistics();
    mss1.combine(mss2);
    mss2.combine(mss1);
    mss1.accept(DateTime.of(1234, 11, 3, 12, 23));
    mss1.combine(mss2);
    mss2.combine(mss1);
    mss1.accept(DateTime.of(1834, 10, 3, 12, 23));
    Tensor mean = mss1.getMean().orElseThrow();
    assertInstanceOf(DateTime.class, mean);
  }
}
