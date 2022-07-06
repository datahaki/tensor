// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.ext.Serialization;
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
import ch.alpine.tensor.qty.UnitConvert;
import ch.alpine.tensor.sca.Chop;

class FrechetDistributionTest {
  @Test
  void testPDF() throws ClassNotFoundException, IOException {
    Distribution distribution = //
        Serialization.copy(FrechetDistribution.of(RealScalar.of(1.3), RealScalar.of(1.2)));
    PDF pdf = PDF.of(distribution);
    Scalar prob = pdf.at(RealScalar.of(2.9));
    Chop._13.requireClose(prob, RealScalar.of(0.10362186999648638));
    assertFalse(pdf.at(RealScalar.of(20000)).equals(RealScalar.ZERO));
  }

  @Test
  void testCDF() {
    Distribution distribution = FrechetDistribution.of(1.5, 1.3);
    CDF cdf = CDF.of(distribution);
    Scalar prob = cdf.p_lessEquals(RealScalar.of(2.3));
    Chop._13.requireClose(prob, RealScalar.of(0.6538117883387893));
  }

  @Test
  void testRandomVariate() {
    FrechetDistribution gmd = (FrechetDistribution) FrechetDistribution.of(3, 0.2);
    assertTrue(Scalars.lessThan(gmd.randomVariate(0), RealScalar.of(0.1)));
    assertTrue(Scalars.lessThan(gmd.randomVariate(Math.nextDown(1.0)), RealScalar.of(42000)));
  }

  @Test
  void testQuantity() {
    Distribution distribution = FrechetDistribution.of(Quantity.of(1.3, ""), Quantity.of(1.4, "m^-1"));
    Scalar rand = RandomVariate.of(distribution);
    assertInstanceOf(Quantity.class, rand);
    UnitConvert.SI().to(Unit.of("in^-1")).apply(rand);
    {
      Scalar prob = PDF.of(distribution).at(Quantity.of(1, "m^-1"));
      QuantityMagnitude.SI().in(Unit.of("in")).apply(prob);
    }
    {
      CDF cdf = CDF.of(distribution);
      Scalar prob = cdf.p_lessEquals(Quantity.of(10, "m^-1"));
      assertInstanceOf(DoubleScalar.class, prob);
      assertTrue(FiniteScalarQ.of(prob));
    }
  }

  @Test
  void testMean() {
    Distribution distribution = //
        FrechetDistribution.of(Quantity.of(1.3, ""), Quantity.of(2.4, "m^-1"));
    Scalar mean = Expectation.mean(distribution);
    Chop._13.requireClose(mean, Quantity.of(9.470020440153482, "m^-1"));
  }

  @Test
  void testMeanInf() {
    Distribution distribution = //
        FrechetDistribution.of(RealScalar.of(0.9), Quantity.of(2.4, "m^-1"));
    Scalar mean = Expectation.mean(distribution);
    assertEquals(mean, Quantity.of(DoubleScalar.POSITIVE_INFINITY, "m^-1"));
  }

  @Test
  void testVariance() {
    Distribution distribution = //
        FrechetDistribution.of(Quantity.of(2.3, ""), Quantity.of(1.5, "m^-1"));
    Scalar var = Expectation.variance(distribution);
    Chop._13.requireClose(var, Quantity.of(10.631533530833654, "m^-2"));
  }

  @Test
  void testVarianceInf() {
    Distribution distribution = //
        FrechetDistribution.of(RealScalar.of(1.3), Quantity.of(1.5, "m^-1"));
    Scalar var = Expectation.variance(distribution);
    assertEquals(var, Quantity.of(Double.POSITIVE_INFINITY, "m^-2"));
    assertEquals(CDF.of(distribution).p_lessThan(RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(CDF.of(distribution).p_lessEquals(RealScalar.ZERO), RealScalar.ZERO);
    assertTrue(distribution.toString().startsWith("FrechetDistribution"));
  }

  @Test
  void testInverseCDF() {
    InverseCDF inverseCDF = InverseCDF.of(FrechetDistribution.of(1.5, 1.3));
    Scalar x0 = inverseCDF.quantile(RealScalar.of(0.0));
    Scalar x1 = inverseCDF.quantile(RealScalar.of(0.1));
    Scalar x2 = inverseCDF.quantile(RealScalar.of(0.2));
    Scalar x3 = inverseCDF.quantile(RealScalar.of(0.5));
    assertEquals(x0, RealScalar.ZERO);
    assertTrue(Scalars.lessThan(x0, x1));
    assertTrue(Scalars.lessThan(x1, x2));
    assertTrue(Scalars.lessThan(x2, x3));
  }

  @Test
  void testMarkov() {
    Random random = new Random();
    Distribution distribution = FrechetDistribution.of(1.1 + random.nextDouble(), 0.1 + random.nextDouble());
    TestMarkovChebyshev.markov(distribution);
    TestMarkovChebyshev.chebyshev(distribution);
  }

  @Test
  void testInverseCDF_1() {
    InverseCDF inverseCDF = InverseCDF.of(FrechetDistribution.of(1.5, 1.3));
    assertThrows(Throw.class, () -> inverseCDF.quantile(RealScalar.of(1.0)));
  }

  @Test
  void testFailInverseCDF() {
    InverseCDF inverseCDF = InverseCDF.of(FrechetDistribution.of(1.5, 1.3));
    assertThrows(Throw.class, () -> inverseCDF.quantile(RealScalar.of(1.1)));
  }

  @Test
  void testFail() {
    assertThrows(Throw.class, () -> FrechetDistribution.of(RealScalar.of(3), RealScalar.of(0)));
    assertThrows(Throw.class, () -> FrechetDistribution.of(RealScalar.of(0), RealScalar.of(2)));
    assertThrows(Throw.class, () -> FrechetDistribution.of(Quantity.of(2.3, "s"), Quantity.of(1.5, "m^-1")));
  }
}
