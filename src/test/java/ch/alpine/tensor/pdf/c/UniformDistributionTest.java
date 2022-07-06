// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.num.Polynomial;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.Expectation;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.red.CentralMoment;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

class UniformDistributionTest {
  @Test
  void testCdf() {
    CDF cdf = CDF.of(UniformDistribution.of(RealScalar.ONE, RealScalar.of(3)));
    assertEquals(cdf.p_lessThan(RealScalar.ONE), RealScalar.ZERO);
    assertEquals(cdf.p_lessThan(RealScalar.of(2)), RationalScalar.of(1, 2));
    assertEquals(cdf.p_lessThan(RealScalar.of(3)), RealScalar.ONE);
    assertEquals(cdf.p_lessThan(RealScalar.of(4)), RealScalar.ONE);
    Scalar prob = cdf.p_lessThan(RealScalar.of(2));
    ExactScalarQ.require(prob);
  }

  private static void _checkCentralMoment(Clip clip) {
    Distribution d1 = UniformDistribution.of(clip);
    Distribution d2 = TrapezoidalDistribution.of(clip.min(), clip.min(), clip.max(), clip.max());
    for (int order = 0; order <= 6; ++order) {
      Scalar uni = CentralMoment.of(d1, order);
      Scalar tra = CentralMoment.of(d2, order);
      assertEquals(uni, tra);
    }
  }

  @Test
  void testCentralMoment() {
    _checkCentralMoment(Clips.interval(-2, 4));
    _checkCentralMoment(Clips.interval(2, 5));
    _checkCentralMoment(Clips.positive(Quantity.of(2, "m")));
  }

  @Test
  void testPdfQuantity() {
    Distribution distribution = UniformDistribution.of(Clips.positive(Quantity.of(2, "m")));
    PDF pdf = PDF.of(distribution);
    assertEquals(pdf.at(Quantity.of(-1, "m")), Quantity.of(0, "m^-1"));
    assertEquals(pdf.at(Quantity.of(+1, "m")), Quantity.of(RationalScalar.HALF, "m^-1"));
  }

  @Test
  void testPdf() {
    UniformDistribution distribution = (UniformDistribution) UniformDistribution.of(1, 3);
    assertEquals(distribution.support(), Clips.interval(1, 3));
    PDF pdf = PDF.of(distribution);
    assertEquals(pdf.at(RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(pdf.at(RealScalar.of(1)), RationalScalar.HALF);
    assertEquals(pdf.at(RealScalar.of(2)), RationalScalar.HALF);
    assertEquals(pdf.at(RealScalar.of(3)), RationalScalar.HALF);
    assertEquals(pdf.at(DoubleScalar.POSITIVE_INFINITY), RealScalar.ZERO);
    assertEquals(CentralMoment.of(distribution, 3), RealScalar.ZERO);
    assertEquals(CentralMoment.of(distribution, 5), RealScalar.ZERO);
    // ---
    assertEquals(CentralMoment.of(distribution, 4), RationalScalar.of(1, 5));
    assertEquals(CentralMoment.of(distribution, 6), RationalScalar.of(1, 7));
  }

  @Test
  void testUnit() throws ClassNotFoundException, IOException {
    UniformDistribution distribution = //
        (UniformDistribution) Serialization.copy(UniformDistribution.unit());
    assertEquals(distribution.mean(), RationalScalar.of(1, 2));
    assertEquals(distribution.variance(), RationalScalar.of(1, 12));
  }

  @Test
  void testRandomVariate() {
    Scalar s1 = RandomVariate.of(UniformDistribution.of(0, 1), new Random(1000));
    Scalar s2 = RandomVariate.of(UniformDistribution.unit(), new Random(1000));
    assertEquals(s1, s2);
  }

  @Test
  void testQuantity() {
    Distribution distribution = UniformDistribution.of(Quantity.of(3, "g"), Quantity.of(5, "g"));
    assertInstanceOf(Quantity.class, RandomVariate.of(distribution));
    Scalar mean = Expectation.mean(distribution);
    assertInstanceOf(Quantity.class, mean);
    assertEquals(mean, Quantity.of(4, "g"));
    Scalar var = Expectation.variance(distribution);
    assertInstanceOf(Quantity.class, var);
    assertEquals(var, Scalars.fromString("1/3[g^2]"));
    {
      Scalar prob = PDF.of(distribution).at(mean);
      QuantityMagnitude.SI().in(Unit.of("lb^-1")).apply(prob);
      assertEquals(prob.toString(), "1/2[g^-1]");
    }
    assertEquals(CDF.of(distribution).p_lessEquals(mean), RationalScalar.of(1, 2));
  }

  @Test
  void testQuantile() {
    Distribution distribution = UniformDistribution.of(Quantity.of(3, "g"), Quantity.of(6, "g"));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    assertEquals(inverseCDF.quantile(RationalScalar.of(0, 3)), Quantity.of(3, "g"));
    assertEquals(inverseCDF.quantile(RationalScalar.of(1, 3)), Quantity.of(4, "g"));
    assertEquals(inverseCDF.quantile(RationalScalar.of(2, 3)), Quantity.of(5, "g"));
    assertEquals(inverseCDF.quantile(RationalScalar.of(3, 3)), Quantity.of(6, "g"));
  }

  @Test
  void testMarkov() {
    Random random = new Random();
    Distribution distribution = UniformDistribution.of(random.nextDouble(), 1 + random.nextDouble());
    TestMarkovChebyshev.markov(distribution);
    TestMarkovChebyshev.chebyshev(distribution);
  }

  @Test
  void testToString() {
    Distribution distribution = UniformDistribution.of(Quantity.of(3, "g"), Quantity.of(6, "g"));
    assertEquals(distribution.toString(), "UniformDistribution[3[g], 6[g]]");
  }

  @Test
  void testClipPointFail() {
    UniformDistribution.of(Clips.interval(3, 5));
    Distribution distribution = UniformDistribution.of(Clips.interval(3, 3));
    Scalar scalar = RandomVariate.of(distribution);
    assertEquals(scalar, RealScalar.of(3));
  }

  @Test
  void testMatchTrapezoidal() {
    Distribution d1 = UniformDistribution.of(Clips.interval(3, 7));
    Distribution d2 = TrapezoidalDistribution.of(3, 3, 7, 7);
    assertEquals(Mean.of(d1), Mean.of(d2));
    assertEquals(Variance.of(d1), Variance.of(d2));
  }

  @Test
  void testMatchTrapezoidalUnit() {
    Scalar a = Quantity.of(4, "m");
    Scalar b = Quantity.of(RationalScalar.of(5 * 3 + 1, 3), "m"); // == 5.3333...
    Distribution d1 = UniformDistribution.of(Clips.interval(a, b));
    Distribution d2 = TrapezoidalDistribution.of(a, a, b, b);
    assertEquals(Mean.of(d1), Mean.of(d2));
    Scalar variance = Variance.of(d1);
    assertEquals(variance, Variance.of(d2));
    assertEquals(variance, CentralMoment.of(d1, 2));
    assertEquals(variance, CentralMoment.of(d2, 2));
  }

  @Test
  void testMoment() {
    Clip clip = Clips.absoluteOne();
    Polynomial polynomial = Polynomial.of(Tensors.fromString("{1/2, 0}"));
    Distribution distribution = UniformDistribution.of(clip);
    for (int order = 0; order < 10; ++order) {
      Scalar cm1 = polynomial.moment(order, clip);
      Scalar cm2 = CentralMoment.of(distribution, order);
      assertEquals(cm1, cm2);
    }
  }

  @Test
  void testClipNullFail() {
    assertThrows(NullPointerException.class, () -> UniformDistribution.of(null));
  }

  @Test
  void testInfiniteFail() {
    Clip clip = Clips.positive(Double.POSITIVE_INFINITY);
    assertThrows(Throw.class, () -> UniformDistribution.of(clip));
  }

  @Test
  void testQuantileFail() {
    Distribution distribution = UniformDistribution.of(Quantity.of(3, "g"), Quantity.of(6, "g"));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    assertThrows(Throw.class, () -> inverseCDF.quantile(RealScalar.of(-0.1)));
    assertThrows(Throw.class, () -> inverseCDF.quantile(RealScalar.of(1.1)));
  }

  @Test
  void testQuantityFail() {
    assertThrows(Throw.class, () -> UniformDistribution.of(Quantity.of(3, "m"), Quantity.of(5, "km")));
  }

  @Test
  void testFail() {
    assertThrows(Throw.class, () -> UniformDistribution.of(RealScalar.ONE, RealScalar.ZERO));
  }
}
