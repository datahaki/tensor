// code by jph
package ch.alpine.tensor.pdf.d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.ScalarSummaryStatistics;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;
import test.wrap.DistributionEquality;
import test.wrap.SerializableQ;

class DiscreteUniformDistributionTest {
  @Test
  void testEquality() {
    Distribution d1 = CategoricalDistribution.fromUnscaledPDF(Tensors.vector(0, 0, 1, 1, 1, 1, 1, 0, 0, 0));
    Distribution d2 = DiscreteUniformDistribution.of(RealScalar.of(2), RealScalar.of(6));
    DistributionEquality distributionEquality = new DistributionEquality(d1, d2);
    distributionEquality.checkRange(-1, 10);
  }

  @RepeatedTest(5)
  void testUniform(RepetitionInfo repetitionInfo) {
    int max = repetitionInfo.getCurrentRepetition();
    Distribution d1 = CategoricalDistribution.fromUnscaledPDF(ConstantArray.of(RealScalar.ONE, max));
    Distribution d2 = DiscreteUniformDistribution.of(0, max - 1);
    new DistributionEquality(d1, d2).checkRange(-4, 10);
  }

  @Test
  void testPdf() {
    Distribution distribution = DiscreteUniformDistribution.of(RealScalar.of(3), RealScalar.of(10));
    SerializableQ.require(distribution);
    PDF pdf = PDF.of(distribution);
    Scalar prob = pdf.at(RealScalar.of(4));
    assertEquals(prob, Rational.of(1, 10 - 3 + 1));
    assertEquals(pdf.at(RealScalar.of(4)), pdf.at(RealScalar.of(8)));
    assertEquals(pdf.at(RealScalar.of(2)), RealScalar.ZERO);
    assertEquals(pdf.at(RealScalar.of(2)), pdf.at(RealScalar.of(11)));
    assertEquals(pdf.at(RealScalar.of(10)), Rational.of(1, 10 - 3 + 1));
    assertEquals(pdf.at(RealScalar.of(11)), RealScalar.ZERO);
  }

  @Test
  void testHereClip() {
    Distribution d2 = DiscreteUniformDistribution.of(Clips.interval(3, 5));
    Distribution d1 = DiscreteUniformDistribution.of(3, 5);
    new DistributionEquality(d1, d2).checkRange(0, 10);
  }

  @Test
  void testLessThan() {
    Distribution distribution = DiscreteUniformDistribution.of(3, 10);
    CDF cdf = CDF.of(distribution);
    assertEquals(cdf.p_lessThan(RealScalar.of(2)), Rational.of(0, 10 - 3 + 1));
    assertEquals(cdf.p_lessThan(RealScalar.of(3)), Rational.of(0, 10 - 3 + 1));
    assertEquals(cdf.p_lessThan(RealScalar.of(3.9)), Rational.of(1, 10 - 3 + 1));
    assertEquals(cdf.p_lessThan(RealScalar.of(4)), Rational.of(1, 10 - 3 + 1));
    assertEquals(cdf.p_lessThan(RealScalar.of(4.1)), Rational.of(2, 10 - 3 + 1));
    assertEquals(cdf.p_lessThan(RealScalar.of(5)), Rational.of(2, 10 - 3 + 1));
    assertEquals(cdf.p_lessThan(RealScalar.of(10)), Rational.of(7, 10 - 3 + 1));
    assertEquals(cdf.p_lessThan(RealScalar.of(11)), Rational.of(8, 10 - 3 + 1));
  }

  @Test
  void testLessEquals() {
    Distribution distribution = DiscreteUniformDistribution.of(RealScalar.of(3), RealScalar.of(10));
    CDF cdf = CDF.of(distribution);
    assertEquals(cdf.p_lessEquals(RealScalar.of(2)), Rational.of(0, 10 - 3 + 1));
    assertEquals(cdf.p_lessEquals(RealScalar.of(3)), Rational.of(1, 10 - 3 + 1));
    assertEquals(cdf.p_lessEquals(RealScalar.of(4)), Rational.of(2, 10 - 3 + 1));
    assertEquals(cdf.p_lessEquals(RealScalar.of(4.1)), Rational.of(2, 10 - 3 + 1));
    assertEquals(cdf.p_lessEquals(RealScalar.of(5)), Rational.of(3, 10 - 3 + 1));
    assertEquals(cdf.p_lessEquals(RealScalar.of(10)), Rational.of(8, 10 - 3 + 1));
    assertEquals(cdf.p_lessEquals(RealScalar.of(11)), Rational.of(8, 10 - 3 + 1));
  }

  @Test
  void testEqualMinMax() {
    DiscreteUniformDistribution.of(RealScalar.of(3), RealScalar.of(3));
    DiscreteUniformDistribution.of(10, 11);
  }

  @Test
  void testInverseCDF() {
    InverseCDF inverseCDF = InverseCDF.of(DiscreteUniformDistribution.of(0, 9));
    Scalar s = inverseCDF.quantile(Rational.of(1, 2));
    assertTrue(Clips.interval(4, 5).isInside(s));
    // assertEquals(inverseCDF.quantile(RealScalar.of(0.9999999)), RealScalar.of(9));
    assertEquals(inverseCDF.quantile(RealScalar.of(1)), RealScalar.of(9));
  }

  @Test
  void testToString() {
    Distribution distribution = DiscreteUniformDistribution.of(3, 9);
    assertEquals(distribution.toString(), "DiscreteUniformDistribution[3, 9]");
  }

  @Test
  void testBigInteger() {
    Scalar min = RealScalar.of(new BigInteger("12323746283746283746283746284"));
    Scalar max = RealScalar.of(new BigInteger("12323746283746283746283746293"));
    Distribution distribution = DiscreteUniformDistribution.of(min, max);
    Tensor tensor = RandomVariate.of(distribution, 100);
    ScalarSummaryStatistics scalarSummaryStatistics = tensor.stream() //
        .map(Scalar.class::cast).collect(ScalarSummaryStatistics.collector());
    scalarSummaryStatistics.toString();
    Clip clip = Clips.interval(min, max);
    clip.requireInside(scalarSummaryStatistics.getMin());
    clip.requireInside(scalarSummaryStatistics.getMax());
  }

  @RepeatedTest(6)
  void testBigInteger2(RepetitionInfo repetitionInfo) {
    Scalar min = RealScalar.of(new BigInteger("12323746283746283746283746284"));
    Scalar max = min.add(RealScalar.of(repetitionInfo.getCurrentRepetition()));
    Distribution distribution = DiscreteUniformDistribution.of(min, max);
    Tensor tensor = RandomVariate.of(distribution, 100);
    ScalarSummaryStatistics scalarSummaryStatistics = tensor.stream() //
        .map(Scalar.class::cast).collect(ScalarSummaryStatistics.collector());
    scalarSummaryStatistics.toString();
    Clip clip = Clips.interval(min, max);
    clip.requireInside(scalarSummaryStatistics.getMin());
    clip.requireInside(scalarSummaryStatistics.getMax());
    assertEquals(scalarSummaryStatistics.getMax(), max);
  }

  @Test
  void testBigSingle() {
    BigInteger bigInteger = new BigInteger("123491827364912736491234978");
    Distribution distribution = DiscreteUniformDistribution.of( //
        bigInteger, //
        bigInteger);
    Scalar x = RealScalar.of(bigInteger);
    assertEquals(Mean.of(distribution), x);
    assertEquals(Variance.of(distribution), RealScalar.ZERO);
    assertEquals(RandomVariate.of(distribution, 3), ConstantArray.of(x, 3));
  }

  @Test
  void testMonotonous() {
    TestMarkovChebyshev.monotonous(DiscreteUniformDistribution.of(3, 9));
    Distribution distribution = DiscreteUniformDistribution.of( //
        new BigInteger("123491827364912736491234978").negate(), //
        new BigInteger("123491827364912736491236789"));
    TestMarkovChebyshev.monotonous(distribution);
    Sign.requirePositive(PDF.of(distribution).at(RealScalar.ZERO));
  }

  @Test
  void testFailZero() {
    DiscreteUniformDistribution.of( //
        new BigInteger("123491827364912736491234978"), //
        new BigInteger("123491827364912736491234978"));
  }

  @Test
  void testFailQuantile() {
    Distribution distribution = DiscreteUniformDistribution.of(3, 9);
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    assertThrows(Throw.class, () -> inverseCDF.quantile(RealScalar.of(-0.1)));
    assertThrows(Throw.class, () -> inverseCDF.quantile(RealScalar.of(1.1)));
  }

  @Test
  void testFailsOrder() {
    assertThrows(Exception.class, () -> DiscreteUniformDistribution.of(RealScalar.of(3), RealScalar.of(2)));
    assertThrows(Exception.class, () -> DiscreteUniformDistribution.of(3, 2));
    DiscreteUniformDistribution.of(3, 3);
  }

  @Test
  void testFailsInt() {
    assertThrows(Throw.class, () -> DiscreteUniformDistribution.of(RealScalar.of(3), RealScalar.of(4.5)));
  }

  @Test
  void testRandomVariate() {
    AbstractDiscreteDistribution distribution = //
        (AbstractDiscreteDistribution) DiscreteUniformDistribution.of(10, 99);
    assertEquals(distribution.quantile(RealScalar.of(Math.nextDown(1.0))), RealScalar.of(99));
    assertEquals(distribution.quantile(RealScalar.of(0)), RealScalar.of(10));
  }
}
