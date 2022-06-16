// code by clruch
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.itp.BSplineFunctionString;
import ch.alpine.tensor.jet.DateTimeScalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

class TrapezoidalDistributionTest {
  final Random random = new Random();

  @Test
  void testPositive() {
    Scalar a = RationalScalar.of(random.nextInt(100), 1);
    Scalar b = a.add(RealScalar.of(random.nextDouble() * 10));
    Scalar c = b.add(RealScalar.of(random.nextDouble() * 10));
    Scalar d = c.add(RealScalar.of(random.nextDouble() * 10));
    Distribution distribution = TrapezoidalDistribution.of(a, b, c, d);
    for (int count = 0; count < 10; ++count) {
      Scalar scalar = RandomVariate.of(distribution);
      assertTrue(Scalars.lessEquals(RealScalar.ZERO, scalar));
    }
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
    assertTrue(distribution.toString().startsWith("TrapezoidalDistribution["));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    assertThrows(TensorRuntimeException.class, () -> inverseCDF.quantile(RealScalar.of(-0.1)));
    assertThrows(TensorRuntimeException.class, () -> inverseCDF.quantile(RealScalar.of(+1.1)));
  }

  @Test
  void testCDFInverseCDF() {
    TrapezoidalDistribution distribution = (TrapezoidalDistribution) TrapezoidalDistribution.of( //
        Quantity.of(1, "m"), Quantity.of(5, "m"), Quantity.of(7, "m"), Quantity.of(11, "m"));
    Clip clip = Clips.interval(Quantity.of(1, "m"), Quantity.of(11, "m"));
    assertEquals(distribution.support(), clip);
    CDF cdf = CDF.of(distribution);
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    for (int count = 0; count < 10; ++count) {
      Scalar x = RandomVariate.of(distribution);
      Scalar q = inverseCDF.quantile(cdf.p_lessEquals(x));
      Tolerance.CHOP.requireClose(x, q);
    }
  }

  @Test
  void testCDFInverseCDF2() {
    TrapezoidalDistribution distribution = (TrapezoidalDistribution) TrapezoidalDistribution.of( //
        Quantity.of(1, "m"), Quantity.of(5, "m"), Quantity.of(5, "m"), Quantity.of(11, "m"));
    Clip clip = Clips.interval(Quantity.of(1, "m"), Quantity.of(11, "m"));
    assertEquals(distribution.support(), clip);
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
    Tolerance.CHOP.requireClose(domain.map(cdf::p_lessEquals), domain.map(suo));
  }

  @Test
  void testMarkov() {
    Random random = new Random();
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
    assertThrows(TensorRuntimeException.class,
        () -> TrapezoidalDistribution.of(Quantity.of(1, "m"), Quantity.of(1, "m"), Quantity.of(1, "m"), Quantity.of(1, "m")));
    assertThrows(TensorRuntimeException.class,
        () -> TrapezoidalDistribution.of(Quantity.of(1, "m"), Quantity.of(2, "m"), Quantity.of(3, "m"), Quantity.of(1, "m")));
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
  @Disabled
  void testDateTimeScalar() {
    DateTimeScalar a = DateTimeScalar.of(LocalDateTime.of(2022, 1, 2, 12, 02));
    DateTimeScalar b = DateTimeScalar.of(LocalDateTime.of(2022, 1, 4, 11, 05));
    DateTimeScalar c = DateTimeScalar.of(LocalDateTime.of(2022, 1, 7, 19, 06));
    DateTimeScalar d = DateTimeScalar.of(LocalDateTime.of(2022, 1, 8, 05, 07));
    Distribution distribution = TrapezoidalDistribution.of(a, b, c, d);
    Scalar scalar = RandomVariate.of(distribution);
    assertInstanceOf(DateTimeScalar.class, scalar);
    PDF pdf = PDF.of(distribution);
    Scalar t = DateTimeScalar.of(LocalDateTime.of(2022, 1, 6, 8, 06));
    pdf.at(t);
    // CDF cdf = CDF.of(distribution);
    // Scalar p_lessEquals = cdf.p_lessEquals(t);
    // Chop._01.requireClose(RationalScalar.HALF, p_lessEquals);
  }

  @Test
  void testNumericFail() {
    TrapezoidalDistribution.of(Quantity.of(1., "m"), Quantity.of(2., "m"), Quantity.of(3., "m"), Quantity.of(3., "m"));
    TrapezoidalDistribution.of(Quantity.of(2., "m"), Quantity.of(2., "m"), Quantity.of(3., "m"), Quantity.of(3., "m"));
    assertThrows(TensorRuntimeException.class,
        () -> TrapezoidalDistribution.of(Quantity.of(1., "m"), Quantity.of(1., "m"), Quantity.of(1., "m"), Quantity.of(1., "m")));
    assertThrows(TensorRuntimeException.class,
        () -> TrapezoidalDistribution.of(Quantity.of(1., "m"), Quantity.of(2., "m"), Quantity.of(3., "m"), Quantity.of(1., "m")));
    TrapezoidalDistribution.of(Quantity.of(1., "m"), Quantity.of(2., "m"), Quantity.of(2., "m"), Quantity.of(5., "m"));
  }

  @Test
  void testCenterFail() {
    assertThrows(TensorRuntimeException.class,
        () -> TrapezoidalDistribution.of(Quantity.of(1., "m"), Quantity.of(3., "m"), Quantity.of(2., "m"), Quantity.of(9., "m")));
  }
}
