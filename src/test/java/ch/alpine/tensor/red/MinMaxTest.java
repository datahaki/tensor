// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Month;
import java.util.Arrays;
import java.util.IntSummaryStatistics;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.jet.DateTime;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Clips;

class MinMaxTest {
  @Test
  void testMembers() {
    MinMax minMax = Tensors.vector(1, 4, 2, 8, 3, 10).stream() //
        .map(Scalar.class::cast).collect(MinMax.collector());
    assertEquals(minMax.getMin(), RealScalar.of(1));
    assertEquals(minMax.getMax(), RealScalar.of(10));
    assertEquals(minMax.getCount(), 6);
  }

  @Test
  void testMembersParallel() {
    MinMax minMax = Tensors.vector(1, 4, 2, 8, 3, 10).stream() //
        .parallel().map(Scalar.class::cast).collect(MinMax.collector());
    assertEquals(minMax.getMin(), RealScalar.of(1));
    assertEquals(minMax.getMax(), RealScalar.of(10));
    assertEquals(minMax.getCount(), 6);
  }

  @Test
  void testQuantity() {
    MinMax stats = Tensors.fromString("{3[s], 11[s], 6[s], 4[s]}").stream() //
        .parallel().map(Scalar.class::cast).collect(MinMax.collector());
    assertEquals(stats.getMin(), Quantity.of(3, "s"));
    assertEquals(stats.getMax(), Quantity.of(11, "s"));
    assertEquals(stats.getCount(), 4);
    assertEquals(stats.toString(), "MinMax[3[s], 11[s]]");
    assertEquals(stats.getClip(), Clips.interval(Quantity.of(3, "s"), Quantity.of(11, "s")));
  }

  @Test
  void testCollector() {
    MinMax stats = Tensors.vector(1, 4, 2, 8, 3, 10).stream() //
        .parallel().map(Scalar.class::cast).collect(MinMax.collector());
    assertEquals(stats.getMin(), RealScalar.of(1));
    assertEquals(stats.getMax(), RealScalar.of(10));
    assertEquals(stats.getCount(), 6);
    stats.toString();
  }

  @Test
  void testEmpty() {
    MinMax stats = Tensors.empty().stream() //
        .parallel().map(Scalar.class::cast).collect(MinMax.collector());
    assertNull(stats.getMin());
    assertNull(stats.getMax());
    assertEquals(stats.getCount(), 0);
    stats.toString();
    assertNull(stats.getClip());
  }

  @Test
  void testEmptyCombine() {
    IntSummaryStatistics iss1 = new IntSummaryStatistics();
    IntSummaryStatistics iss2 = new IntSummaryStatistics();
    iss1.combine(iss2);
    ScalarSummaryStatistics sss1 = new ScalarSummaryStatistics();
    ScalarSummaryStatistics sss2 = new ScalarSummaryStatistics();
    sss1.combine(sss2);
  }

  @Test
  void testSemiCombine() {
    IntSummaryStatistics iss1 = new IntSummaryStatistics();
    IntSummaryStatistics iss2 = Arrays.asList(3, 2).stream() //
        .mapToInt(Integer::intValue) //
        .summaryStatistics();
    iss1.combine(iss2);
    MinMax sss1 = new MinMax();
    MinMax sss2 = Tensors.vector(1, 4, 2, 8, 3, 10).stream() //
        .parallel().map(Scalar.class::cast).collect(MinMax.collector());
    sss1.combine(sss2);
  }

  @Test
  void testGaussian() {
    Tensor vector = Tensors.of( //
        GaussScalar.of(2, 7), //
        GaussScalar.of(3, 7), //
        GaussScalar.of(5, 7), //
        GaussScalar.of(1, 7));
    MinMax minMax = //
        vector.stream().parallel().map(Scalar.class::cast).collect(MinMax.collector());
    assertEquals(minMax.getCount(), 4);
    assertEquals(minMax.getMin(), GaussScalar.of(1, 7));
    assertEquals(minMax.getMax(), GaussScalar.of(5, 7));
  }

  @Test
  void testGaussian2() {
    Tensor vector = Tensors.of( //
        GaussScalar.of(3, 7), //
        GaussScalar.of(5, 7), //
        GaussScalar.of(1, 7));
    MinMax minMax = //
        vector.stream().parallel().map(Scalar.class::cast).collect(MinMax.collector());
    assertEquals(minMax.getCount(), 3);
    assertEquals(minMax.getMin(), GaussScalar.of(1, 7));
    assertEquals(minMax.getMax(), GaussScalar.of(5, 7));
    minMax.toString();
  }

  @Test
  void testDateTime() {
    Distribution distribution = UniformDistribution.of( //
        DateTime.of(1980, Month.APRIL, 3, 4, 12), //
        DateTime.of(2010, Month.SEPTEMBER, 9, 4, 12));
    Tensor vector = RandomVariate.of(distribution, 100);
    MinMax minMax = vector.stream().map(Scalar.class::cast).collect(MinMax.collector());
    assertNotNull(minMax.getClip());
    minMax.toString();
    assertInstanceOf(DateTime.class, minMax.getClip().min());
  }
}
