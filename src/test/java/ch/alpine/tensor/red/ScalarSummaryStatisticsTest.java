// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.time.Month;
import java.util.Arrays;
import java.util.IntSummaryStatistics;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.DateTime;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.red.ScalarSummaryStatistics.ScalarSummaryStatisticsCollector;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;

class ScalarSummaryStatisticsTest {
  @Test
  void testMembers() {
    Tensor vector = Tensors.vector(1, 4, 2, 8, 3, 10);
    ScalarSummaryStatistics scalarSummaryStatistics = vector //
        .stream().map(Scalar.class::cast).collect(ScalarSummaryStatistics.collector());
    assertEquals(scalarSummaryStatistics.getSum(), RealScalar.of(28));
    assertEquals(scalarSummaryStatistics.getMin(), RealScalar.of(1));
    assertEquals(scalarSummaryStatistics.getMax(), RealScalar.of(10));
    assertEquals(scalarSummaryStatistics.getMean(), Rational.of(14, 3));
    {
      Scalar var1 = Variance.ofVector(vector);
      Scalar var2 = scalarSummaryStatistics.getVariance();
      assertEquals(var1, var2);
    }
    {
      Scalar var1 = StandardDeviation.ofVector(vector);
      Scalar var2 = scalarSummaryStatistics.getStandardDeviation();
      assertEquals(var1, var2);
    }
  }

  @Test
  void testMembersParallel() {
    ScalarSummaryStatistics scalarSummaryStatistics = Tensors.vector(1, 4, 2, 8, 3, 10) //
        .stream().parallel().map(Scalar.class::cast).collect(ScalarSummaryStatistics.collector());
    assertEquals(scalarSummaryStatistics.getSum(), RealScalar.of(28));
    assertEquals(scalarSummaryStatistics.getMin(), RealScalar.of(1));
    assertEquals(scalarSummaryStatistics.getMax(), RealScalar.of(10));
    assertEquals(scalarSummaryStatistics.getMean(), Rational.of(14, 3));
  }

  @Test
  void testQuantity() {
    Tensor vector = Tensors.fromString("{3[s], 11[s], 6[s], 4[s]}");
    ScalarSummaryStatistics stats = vector.stream() //
        .parallel().map(Scalar.class::cast).collect(ScalarSummaryStatistics.collector());
    assertEquals(stats.getSum(), Quantity.of(24, "s"));
    assertEquals(stats.getMin(), Quantity.of(3, "s"));
    assertEquals(stats.getMax(), Quantity.of(11, "s"));
    assertEquals(stats.getMean(), Quantity.of(6, "s"));
    stats.toString();
    assertEquals(stats.getClip(), Clips.interval(Quantity.of(3, "s"), Quantity.of(11, "s")));
    {
      Scalar var1 = Variance.ofVector(vector);
      Scalar var2 = stats.getVariance();
      assertEquals(var1, var2);
      ExactScalarQ.require(var2);
    }
    {
      Scalar var1 = StandardDeviation.ofVector(vector);
      Scalar var2 = stats.getStandardDeviation();
      Tolerance.CHOP.requireClose(var1, var2);
    }
  }

  @Test
  void testCollector() {
    ScalarSummaryStatistics stats = Tensors.vector(1, 4, 2, 8, 3, 10).stream() //
        .parallel().map(Scalar.class::cast).collect(ScalarSummaryStatistics.collector());
    assertEquals(stats.getSum(), RealScalar.of(28));
    assertEquals(stats.getMin(), RealScalar.of(1));
    assertEquals(stats.getMax(), RealScalar.of(10));
    assertEquals(stats.getMean(), Rational.of(14, 3));
    stats.toString();
  }

  @Test
  void testEmpty() {
    ScalarSummaryStatistics stats = Tensors.empty().stream() //
        .parallel().map(Scalar.class::cast).collect(ScalarSummaryStatistics.collector());
    assertNull(stats.getSum());
    assertNull(stats.getMin());
    assertNull(stats.getMax());
    assertNull(stats.getMean());
    assertNull(stats.getVariance());
    // assertThrows(Exception.class, () -> stats.getVariance());
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
    ScalarSummaryStatistics sss1 = new ScalarSummaryStatistics();
    ScalarSummaryStatistics sss2 = Tensors.vector(1, 4, 2, 8, 3, 10).stream() //
        .parallel().map(Scalar.class::cast).collect(ScalarSummaryStatistics.collector());
    sss1.combine(sss2);
  }

  @Test
  void testRandom() {
    Tensor vector = RandomVariate.of(UniformDistribution.unit(), 100);
    ScalarSummaryStatistics ss1 = vector.stream().map(Scalar.class::cast).collect(ScalarSummaryStatistics.collector());
    ScalarSummaryStatistics ss2 = vector.stream().parallel().map(Scalar.class::cast).collect(ScalarSummaryStatistics.collector());
    Tensor mean = Mean.of(vector);
    Chop._14.requireClose(ss1.getMean(), ss2.getMean());
    Chop._14.requireClose(ss1.getMean(), mean);
  }

  @Test
  void testGaussian() {
    Tensor vector = Tensors.of( //
        GaussScalar.of(2, 7), //
        GaussScalar.of(3, 7), //
        GaussScalar.of(5, 7), //
        GaussScalar.of(1, 7));
    ScalarSummaryStatistics sss1 = //
        vector.stream().parallel().map(Scalar.class::cast).collect(ScalarSummaryStatistics.collector());
    Scalar sum = sss1.getSum();
    assertEquals(sum, GaussScalar.of(4, 7));
    assertEquals(sss1.getMean(), GaussScalar.of(1, 7));
  }

  @Test
  void testGaussian2() {
    Tensor vector = Tensors.of( //
        GaussScalar.of(3, 7), //
        GaussScalar.of(5, 7), //
        GaussScalar.of(1, 7));
    ScalarSummaryStatistics sss1 = //
        vector.stream().parallel().map(Scalar.class::cast).collect(ScalarSummaryStatistics.collector());
    Scalar sum = sss1.getSum();
    assertEquals(sum, GaussScalar.of(2, 7));
    assertEquals(sss1.getMean(), GaussScalar.of(3, 7));
    sss1.toString();
  }

  @Test
  void testDateTime() {
    Distribution distribution = UniformDistribution.of( //
        DateTime.of(1980, Month.APRIL, 3, 4, 12), //
        DateTime.of(2010, Month.SEPTEMBER, 9, 4, 12));
    Tensor vector = RandomVariate.of(distribution, 100);
    ScalarSummaryStatistics sss1 = vector.stream().map(Scalar.class::cast).collect(ScalarSummaryStatistics.collector());
    sss1.getClip();
    sss1.toString();
    Tolerance.CHOP.requireClose(Mean.ofVector(vector), sss1.getMean());
    assertInstanceOf(DateTime.class, sss1.getMean());
    assertThrows(Exception.class, sss1::getSum);
    Scalar variance = sss1.getVariance();
    assertEquals(QuantityUnit.of(variance), Unit.of("s^2"));
    // assertEquals(variance, Variance.ofVector(vector));
  }

  @Test
  void testFinisher() {
    assertNull(ScalarSummaryStatisticsCollector.INSTANCE.finisher().apply(null));
  }

  @Test
  void testFinal() {
    assertTrue(Modifier.isFinal(ScalarSummaryStatistics.class.getModifiers()));
  }
}
