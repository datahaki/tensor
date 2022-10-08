// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.time.Month;
import java.util.Arrays;
import java.util.IntSummaryStatistics;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.DateTime;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.MinMax.MinMaxCollector;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

class MinMaxTest {
  @Test
  void testMembers() {
    MinMax minMax = Tensors.vector(1, 4, 2, 8, 3, 10).stream() //
        .map(Scalar.class::cast).collect(MinMax.collector());
    assertEquals(minMax.min(), RealScalar.of(1));
    assertEquals(minMax.max(), RealScalar.of(10));
    // assertEquals(minMax.count(), 6);
  }

  @Test
  void testMembersParallel() {
    MinMax minMax = Tensors.vector(1, 4, 2, 8, 3, 10).stream() //
        .parallel().map(Scalar.class::cast).collect(MinMax.collector());
    assertEquals(minMax.min(), RealScalar.of(1));
    assertEquals(minMax.max(), RealScalar.of(10));
    // assertEquals(minMax.count(), 6);
  }

  @Test
  void testClip() {
    Clip clip = Tensors.fromString("{3[s], 11[s], 6[s], 4[s]}").stream() //
        .map(Scalar.class::cast).collect(MinMax.toClip());
    assertEquals(clip, Clips.interval(Quantity.of(3, "s"), Quantity.of(11, "s")));
  }

  @Test
  void testQuantity() {
    MinMax stats = Tensors.fromString("{3[s], 11[s], 6[s], 4[s]}").stream() //
        .parallel().map(Scalar.class::cast).collect(MinMax.collector());
    assertEquals(stats.min(), Quantity.of(3, "s"));
    assertEquals(stats.max(), Quantity.of(11, "s"));
    // assertEquals(stats.count(), 4);
    assertEquals(stats.toString(), "MinMax[3[s], 11[s]]");
    assertEquals(stats.clip(), Clips.interval(Quantity.of(3, "s"), Quantity.of(11, "s")));
  }

  @Test
  void testCollector() {
    MinMax stats = Tensors.vector(1, 4, 2, 8, 3, 10).stream() //
        .parallel().map(Scalar.class::cast).collect(MinMax.collector());
    assertEquals(stats.min(), RealScalar.of(1));
    assertEquals(stats.max(), RealScalar.of(10));
    // assertEquals(stats.count(), 6);
    stats.toString();
  }

  @Test
  void testEmpty() {
    MinMax stats = Tensors.empty().stream() //
        .parallel().map(Scalar.class::cast).collect(MinMax.collector());
    assertNull(stats.min());
    assertNull(stats.max());
    assertNull(stats.clip());
    // assertEquals(stats.count(), 0);
    assertEquals(stats.toString(), "MinMax[null, null]");
  }

  @Test
  void testEmptyCombineInt() {
    IntSummaryStatistics iss1 = new IntSummaryStatistics();
    IntSummaryStatistics iss2 = new IntSummaryStatistics();
    iss1.combine(iss2);
    ScalarSummaryStatistics sss1 = new ScalarSummaryStatistics();
    ScalarSummaryStatistics sss2 = new ScalarSummaryStatistics();
    sss1.combine(sss2);
  }

  @Test
  void testEmptyCombineMinMax() {
    MinMax x = new MinMax();
    assertSame(x.combine(new MinMax()), x);
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
    // assertEquals(minMax.count(), 4);
    assertEquals(minMax.min(), GaussScalar.of(1, 7));
    assertEquals(minMax.max(), GaussScalar.of(5, 7));
  }

  @Test
  void testGaussian2() {
    Tensor vector = Tensors.of( //
        GaussScalar.of(3, 7), //
        GaussScalar.of(5, 7), //
        GaussScalar.of(1, 7));
    MinMax minMax = //
        vector.stream().parallel().map(Scalar.class::cast).collect(MinMax.collector());
    // assertEquals(minMax.count(), 3);
    assertEquals(minMax.min(), GaussScalar.of(1, 7));
    assertEquals(minMax.max(), GaussScalar.of(5, 7));
    minMax.toString();
  }

  @Test
  void testDateTime() {
    Distribution distribution = UniformDistribution.of( //
        DateTime.of(1980, Month.APRIL, 3, 4, 12), //
        DateTime.of(2010, Month.SEPTEMBER, 9, 4, 12));
    Tensor vector = RandomVariate.of(distribution, 100);
    MinMax minMax = vector.stream().map(Scalar.class::cast).collect(MinMax.collector());
    assertNotNull(minMax.clip());
    minMax.toString();
    assertInstanceOf(DateTime.class, minMax.clip().min());
  }

  @Test
  void testFinal() {
    assertTrue(Modifier.isFinal(MinMax.class.getModifiers()));
  }

  @Test
  void test() {
    assertFalse(Modifier.isPublic(MinMaxCollector.class.getModifiers()));
  }
}
