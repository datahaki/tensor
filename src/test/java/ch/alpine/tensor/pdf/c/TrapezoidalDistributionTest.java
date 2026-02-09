// code by clruch, jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.itp.BSplineFunctionString;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.pdf.UnivariateDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.qty.DateTime;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Clips;

class TrapezoidalDistributionTest {
  @Test
  void testPositive() {
    Random random = ThreadLocalRandom.current();
    Scalar a = RationalScalar.of(random.nextInt(100), 1);
    Scalar b = a.add(RealScalar.of(random.nextDouble() * 10));
    Scalar c = b.add(RealScalar.of(random.nextDouble() * 10));
    Scalar d = c.add(RealScalar.of(random.nextDouble() * 10));
    Distribution distribution = TrapezoidalDistribution.of(a, b, c, d);
    for (int count = 0; count < 10; ++count) {
      Scalar scalar = RandomVariate.of(distribution);
      assertTrue(Scalars.lessEquals(RealScalar.ZERO, scalar));
    }
    assertTrue(distribution.toString().startsWith("TransformedDistribution[TrapezoidalDistribution["));
  }

  @Test
  void testPDF() throws ClassNotFoundException, IOException {
    Scalar a = RationalScalar.of(1, 1);
    Scalar b = RationalScalar.of(2, 1);
    Scalar c = RationalScalar.of(3, 1);
    Scalar d = RationalScalar.of(4, 1);
    Distribution distribution = Serialization.copy(TrapezoidalDistribution.of(a, b, c, d));
    {
      Scalar actual = PDF.of(distribution).at(RealScalar.of(3));
      Scalar expected = RealScalar.of(2).divide(d.add(c).subtract(a).subtract(b));
      assertEquals(expected, actual);
    }
    {
      assertEquals(PDF.of(distribution).at(RealScalar.of(-3)), RealScalar.ZERO);
      assertEquals(PDF.of(distribution).at(RealScalar.of(13)), RealScalar.ZERO);
    }
  }

  @Test
  void testCDFPositive() {
    Scalar a = RealScalar.of(1);
    Scalar b = RealScalar.of(2);
    Scalar c = RealScalar.of(3);
    Scalar d = RealScalar.of(4);
    Distribution distribution = TrapezoidalDistribution.of(a, b, c, d);
    // assertEquals(distribution.toString(), "TrapezoidalDistribution[1, 2, 3, 4]");
    CDF cdf = CDF.of(distribution);
    assertEquals(cdf.p_lessEquals(RealScalar.of(-1)), RealScalar.ZERO);
    assertEquals(cdf.p_lessEquals(RealScalar.of(1.5)), RationalScalar.of(1, 16));
    assertEquals(cdf.p_lessEquals(RealScalar.of(+4)), RealScalar.ONE);
  }

  @Test
  void testMean() {
    Scalar a = RandomVariate.of(DiscreteUniformDistribution.of(0, 100));
    Distribution paramDist = UniformDistribution.of(0, 10);
    Scalar b = a.add(RandomVariate.of(paramDist));
    Scalar c = b.add(RandomVariate.of(paramDist));
    Scalar d = c.add(RandomVariate.of(paramDist));
    Distribution distribution = TrapezoidalDistribution.of(a, b, c, d);
    Tensor all = RandomVariate.of(distribution, 3000);
    Scalar meanCalc = Mean.of(distribution);
    Scalar meanSamples = Mean.ofVector(all);
    Scalar diff = Abs.between(meanCalc, meanSamples);
    assertTrue(Scalars.lessEquals(diff, RealScalar.of(0.5)));
  }

  @Test
  void testVariance() {
    // values confirmed with Mathematica
    assertEquals(Variance.of(TrapezoidalDistribution.of(1, 2, 3, 4)), RationalScalar.of(5, 12));
    assertEquals(Variance.of(TrapezoidalDistribution.of(1, 2, 4, 4)), RationalScalar.of(253, 450));
    assertEquals(Variance.of(TrapezoidalDistribution.of(1, 2, 4, 7)), RationalScalar.of(251, 144));
    assertEquals(Variance.of(TrapezoidalDistribution.of(2, 2, 4, 13)), RationalScalar.of(6719, 1014));
    assertEquals(Variance.of(TrapezoidalDistribution.of(-1, -1, 1, 1)), RationalScalar.of(1, 3));
  }

  @Test
  void testSymmetry() {
    TestMarkovChebyshev.symmetricAroundMean(TrapezoidalDistribution.of(1, 2, 5, 6));
  }

  @Test
  void testQuantity() {
    Distribution distribution = //
        TrapezoidalDistribution.of(Quantity.of(1, "m"), Quantity.of(2, "m"), Quantity.of(3, "m"), Quantity.of(5, "m"));
    Scalar mean = Mean.of(distribution);
    assertEquals(mean, Scalars.fromString("14/5[m]"));
    ExactScalarQ.require(mean);
    PDF pdf = PDF.of(distribution);
    {
      Scalar density = pdf.at(Quantity.of(3, "m"));
      assertEquals(density, Scalars.fromString("2/5[m^-1]"));
    }
    {
      Scalar density = pdf.at(Quantity.of(-3, "m"));
      assertEquals(density, Scalars.fromString("0[m^-1]"));
    }
    CDF cdf = CDF.of(distribution);
    {
      Scalar prob = cdf.p_lessEquals(Quantity.of(4, "m"));
      ExactScalarQ.require(prob);
      assertEquals(prob, RationalScalar.of(9, 10));
    }
    {
      Scalar prob = cdf.p_lessEquals(Quantity.of(6, "m"));
      ExactScalarQ.require(prob);
      assertEquals(prob, RealScalar.ONE);
    }
    Scalar random = RandomVariate.of(distribution);
    Scalar apply = QuantityMagnitude.SI().in("km").apply(random);
    assertInstanceOf(RealScalar.class, apply);
    Scalar variance = Variance.of(distribution);
    ExactScalarQ.require(variance);
  }

  @Test
  void testQuantity2() {
    Distribution distribution = //
        TrapezoidalDistribution.of(Quantity.of(1, "m"), Quantity.of(2, "m"), Quantity.of(3, "m"), Quantity.of(4, "m"));
    Scalar mean = Mean.of(distribution);
    assertEquals(mean, Scalars.fromString("5/2[m]"));
    ExactScalarQ.require(mean);
    PDF pdf = PDF.of(distribution);
    {
      Scalar density = pdf.at(Scalars.fromString("3/2[m]"));
      ExactScalarQ.require(density);
      assertEquals(density, Scalars.fromString("1/4[m^-1]"));
    }
    {
      Scalar density = pdf.at(Quantity.of(2.5, "m"));
      // ExactScalarQ.require(density);
      assertEquals(density, Scalars.fromString("1/2[m^-1]"));
    }
    {
      Scalar density = pdf.at(Scalars.fromString("7/2[m]"));
      ExactScalarQ.require(density);
      assertEquals(density, Scalars.fromString("1/4[m^-1]"));
    }
    CDF cdf = CDF.of(distribution);
    {
      Scalar prob = cdf.p_lessEquals(Quantity.of(4, "m"));
      ExactScalarQ.require(prob);
      assertEquals(prob, RationalScalar.of(10, 10));
    }
    {
      Scalar prob = cdf.p_lessEquals(Quantity.of(6, "m"));
      ExactScalarQ.require(prob);
      assertEquals(prob, RealScalar.ONE);
    }
    Scalar random = RandomVariate.of(distribution);
    Scalar apply = QuantityMagnitude.SI().in("km").apply(random);
    assertInstanceOf(RealScalar.class, apply);
    // assertTrue(distribution.toString().startsWith("TrapezoidalDistribution["));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    assertThrows(Throw.class, () -> inverseCDF.quantile(RealScalar.of(-0.1)));
    assertThrows(Throw.class, () -> inverseCDF.quantile(RealScalar.of(+1.1)));
    TestMarkovChebyshev.symmetricAroundMean(distribution);
  }

  @Test
  void testCDFInverseCDF() {
    Distribution distribution = TrapezoidalDistribution.of( //
        Quantity.of(1, "m"), Quantity.of(5, "m"), Quantity.of(7, "m"), Quantity.of(11, "m"));
    CDF cdf = CDF.of(distribution);
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    assertEquals(inverseCDF.quantile(RealScalar.ZERO), Quantity.of(1, "m"));
    assertEquals(inverseCDF.quantile(RealScalar.ONE), Quantity.of(11, "m"));
    for (int count = 0; count < 10; ++count) {
      Scalar x = RandomVariate.of(distribution);
      Scalar q = inverseCDF.quantile(cdf.p_lessEquals(x));
      Tolerance.CHOP.requireClose(x, q);
    }
  }

  @Test
  void testCDFInverseCDF2() {
    Distribution distribution = TrapezoidalDistribution.of( //
        Quantity.of(1, "m"), Quantity.of(5, "m"), Quantity.of(5, "m"), Quantity.of(11, "m"));
    CDF cdf = CDF.of(distribution);
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    for (int count = 0; count < 10; ++count) {
      Scalar x = RandomVariate.of(distribution);
      Scalar q = inverseCDF.quantile(cdf.p_lessEquals(x));
      Tolerance.CHOP.requireClose(x, q);
    }
    ExactScalarQ.require(Mean.of(distribution));
    ExactScalarQ.require(Variance.of(distribution));
    TestMarkovChebyshev.chebyshev(distribution);
    TestMarkovChebyshev.markov(distribution);
  }

  @Test
  void testBSpline2() {
    Distribution distribution = TrapezoidalDistribution.of(0.5, 1.5, 1.5, 2.5);
    CDF cdf = CDF.of(distribution);
    Tensor sequence = Tensors.vector(0, 0, 1, 1);
    Tensor domain = Subdivide.of(0, sequence.length() - 1, 100);
    ScalarTensorFunction suo = BSplineFunctionString.of(2, sequence);
    Tolerance.CHOP.requireClose(domain.maps(cdf::p_lessEquals), domain.maps(suo));
  }

  @Test
  void testMarkov() {
    Random random = ThreadLocalRandom.current();
    Distribution distribution = TrapezoidalDistribution.of( //
        0 + random.nextDouble(), //
        1 + random.nextDouble(), //
        2 + random.nextDouble(), //
        3 + random.nextDouble());
    TestMarkovChebyshev.markov(distribution);
  }

  @Test
  void testExactFail() {
    Distribution distribution = TrapezoidalDistribution.of(Quantity.of(1, "m"), Quantity.of(2, "m"), Quantity.of(3, "m"), Quantity.of(3, "m"));
    TestMarkovChebyshev.chebyshev(distribution);
    TestMarkovChebyshev.markov(distribution);
    TrapezoidalDistribution.of(Quantity.of(2, "m"), Quantity.of(2, "m"), Quantity.of(3, "m"), Quantity.of(3, "m"));
    assertThrows(Throw.class, () -> TrapezoidalDistribution.of(Quantity.of(1, "m"), Quantity.of(1, "m"), Quantity.of(1, "m"), Quantity.of(1, "m")));
    assertThrows(Throw.class, () -> TrapezoidalDistribution.of(Quantity.of(1, "m"), Quantity.of(2, "m"), Quantity.of(3, "m"), Quantity.of(1, "m")));
    TrapezoidalDistribution.of(Quantity.of(1, "m"), Quantity.of(2, "m"), Quantity.of(2, "m"), Quantity.of(5, "m"));
  }

  @Test
  void testObviousMean() {
    Distribution distribution = TrapezoidalDistribution.of(4, 5, 6, 7);
    Scalar mean = Mean.of(distribution);
    assertEquals(mean, RationalScalar.of(11, 2));
    ExactScalarQ.require(mean);
    assertEquals(Variance.of(distribution), RationalScalar.of(5, 12));
    TestMarkovChebyshev.chebyshev(distribution);
    TestMarkovChebyshev.markov(distribution);
  }

  @Test
  void testTriangularVar() {
    Distribution distribution = TrapezoidalDistribution.of(4, 5, 5, 7);
    Scalar mean = Mean.of(distribution);
    assertEquals(mean, RationalScalar.of(16, 3));
    ExactScalarQ.require(mean);
    assertEquals(Variance.of(distribution), RationalScalar.of(7, 18));
    TestMarkovChebyshev.chebyshev(distribution);
    TestMarkovChebyshev.markov(distribution);
  }

  @Test
  void testWithMean() {
    Distribution distribution = TrapezoidalDistribution.with(4, 3, 2);
    Scalar mean = Mean.of(distribution);
    Tolerance.CHOP.requireClose(mean, RealScalar.of(4));
    Tolerance.CHOP.requireClose(Variance.of(distribution), RealScalar.of(9));
  }

  @Test
  void testDateTime() {
    DateTime a = DateTime.of(2022, 1, 2, 12, 2);
    DateTime b = DateTime.of(2022, 1, 4, 11, 5);
    DateTime c = DateTime.of(2022, 1, 7, 19, 6);
    DateTime d = DateTime.of(2022, 1, 8, 5, 7);
    Distribution distribution = TrapezoidalDistribution.of(a, b, c, d);
    Scalar scalar = RandomVariate.of(distribution);
    assertInstanceOf(DateTime.class, scalar);
    PDF pdf = PDF.of(distribution);
    Scalar t = DateTime.of(2022, 1, 6, 8, 6);
    pdf.at(t);
    CDF cdf = CDF.of(distribution);
    Scalar p_lessEquals = cdf.p_lessEquals(t);
    assertEquals(p_lessEquals, RationalScalar.of(8225, 13026));
    // Chop._01.requireClose(RationalScalar.HALF, p_lessEquals);
    UnivariateDistribution ud = (UnivariateDistribution) distribution;
    assertEquals(ud.support(), Clips.interval(a, d));
  }

  @Test
  void testMonotonous() {
    TestMarkovChebyshev.monotonous(TrapezoidalDistribution.of(0.2, 3, 4, 6));
  }

  @Test
  void testNumericFail() {
    TrapezoidalDistribution.of(Quantity.of(1., "m"), Quantity.of(2., "m"), Quantity.of(3., "m"), Quantity.of(3., "m"));
    TrapezoidalDistribution.of(Quantity.of(2., "m"), Quantity.of(2., "m"), Quantity.of(3., "m"), Quantity.of(3., "m"));
    assertThrows(Throw.class, () -> TrapezoidalDistribution.of(Quantity.of(1., "m"), Quantity.of(1., "m"), Quantity.of(1., "m"), Quantity.of(1., "m")));
    assertThrows(Throw.class, () -> TrapezoidalDistribution.of(Quantity.of(1., "m"), Quantity.of(2., "m"), Quantity.of(3., "m"), Quantity.of(1., "m")));
    TrapezoidalDistribution.of(Quantity.of(1., "m"), Quantity.of(2., "m"), Quantity.of(2., "m"), Quantity.of(5., "m"));
  }

  @Test
  void testCenterFail() {
    assertThrows(Throw.class, () -> TrapezoidalDistribution.of(Quantity.of(1., "m"), Quantity.of(3., "m"), Quantity.of(2., "m"), Quantity.of(9., "m")));
  }
}
