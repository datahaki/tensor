// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.Expectation;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.pdf.d.BinomialDistribution;
import ch.alpine.tensor.qty.DateTime;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.qty.UnitConvert;
import ch.alpine.tensor.red.CentralMoment;
import ch.alpine.tensor.red.Kurtosis;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.StandardDeviation;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Sign;

class NormalDistributionTest {
  @Test
  void testExpectationMean() {
    Scalar mean = RationalScalar.of(3, 5);
    Distribution distribution = NormalDistribution.of(mean, RationalScalar.of(4, 9));
    assertEquals(Expectation.mean(distribution), mean);
  }

  @Test
  void testPDF() {
    Scalar mean = RationalScalar.of(3, 5);
    Scalar sigma = RationalScalar.of(4, 9);
    Distribution distribution = NormalDistribution.of(mean, sigma);
    PDF pdf = PDF.of(distribution);
    Scalar delta = RationalScalar.of(2, 3);
    // for delta with numerical precision, a small deviation is introduced
    assertEquals(pdf.at(mean.subtract(delta)), pdf.at(mean.add(delta)));
    // 0.8976201309032235253648786348523592040707
    assertTrue(pdf.at(mean).toString().startsWith("0.89762013090322"));
    TestMarkovChebyshev.symmetricAroundMean(distribution);
  }

  @Test
  void testFit() {
    Distribution distribution = BinomialDistribution.of(1000, RealScalar.of(1 / 3.));
    Distribution normal = NormalDistribution.fit(distribution);
    assertEquals(Expectation.mean(distribution), Expectation.mean(normal));
    Chop._12.requireClose(Expectation.variance(distribution), Expectation.variance(normal));
  }

  @Test
  void testCdf() {
    CDF cdf = (CDF) NormalDistribution.of(RealScalar.of(-10.2), RealScalar.of(2.3));
    Scalar x = RealScalar.of(-11);
    Scalar s = cdf.p_lessThan(x);
    assertEquals(s, cdf.p_lessEquals(x));
    assertTrue(s.toString().startsWith("0.363985"));
  }

  @Test
  void testQuantity() {
    Distribution distribution = NormalDistribution.of(Quantity.of(3, "m"), Quantity.of(2, "m"));
    assertInstanceOf(Quantity.class, RandomVariate.of(distribution));
    Scalar mean = Expectation.mean(distribution);
    assertInstanceOf(Quantity.class, mean);
    Scalar var = Expectation.variance(distribution);
    assertInstanceOf(Quantity.class, var);
    assertEquals(QuantityMagnitude.SI().in(Unit.of("m^2")).apply(var), RealScalar.of(4));
    {
      Scalar prob = PDF.of(distribution).at(mean);
      QuantityMagnitude.SI().in(Unit.of("in^-1")).apply(prob);
    }
    Chop._07.requireClose( //
        CDF.of(distribution).p_lessEquals(mean), //
        RationalScalar.of(1, 2));
    TestMarkovChebyshev.symmetricAroundMean(distribution);
  }

  @Test
  void testToString() {
    Distribution distribution = NormalDistribution.of(Quantity.of(3, "m"), Quantity.of(2, "m"));
    String string = distribution.toString();
    assertEquals(string, "NormalDistribution[3[m], 2[m]]");
    TestMarkovChebyshev.symmetricAroundMean(distribution);
  }

  @Test
  void testCDFInverseCDF() {
    Distribution distribution = NormalDistribution.of(3, 0.2);
    CDF cdf = CDF.of(distribution);
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    for (int count = 0; count < 10; ++count) {
      Scalar x = RandomVariate.of(distribution);
      Scalar q = inverseCDF.quantile(cdf.p_lessEquals(x));
      Tolerance.CHOP.requireClose(x, q);
    }
  }

  @Test
  void testChebyshev() {
    TestMarkovChebyshev.chebyshev(NormalDistribution.standard());
    TestMarkovChebyshev.chebyshev(NormalDistribution.of(3, 2));
    TestMarkovChebyshev.chebyshev(NormalDistribution.of(3, 0.5));
  }

  @Test
  void testCentralMoment() {
    Distribution distribution = NormalDistribution.of(Quantity.of(3, "m"), Quantity.of(2, "m"));
    assertEquals(CentralMoment.of(distribution, 0), RealScalar.ONE);
    assertEquals(CentralMoment.of(distribution, 2), Quantity.of(4, "m^2"));
    assertEquals(CentralMoment.of(distribution, 4), Quantity.of(48, "m^4"));
    assertEquals(CentralMoment.of(distribution, 6), Quantity.of(960, "m^6"));
    assertEquals(CentralMoment.of(distribution, 1), Quantity.of(0, "m^1"));
    assertEquals(CentralMoment.of(distribution, 3), Quantity.of(0, "m^3"));
    assertEquals(CentralMoment.of(distribution, 5), Quantity.of(0, "m^5"));
  }

  @Test
  void testKurtosis() {
    assertEquals(Kurtosis.of(NormalDistribution.of(3, 4)), RealScalar.of(3));
  }

  @Test
  void testDateTime() {
    DateTime mean = DateTime.of(2001, 9, 11, 3, 2, 3);
    Scalar sigma = Quantity.of(123, "s");
    Distribution distribution = NormalDistribution.of(mean, sigma);
    Scalar scalar = RandomVariate.of(distribution);
    assertInstanceOf(DateTime.class, scalar);
    PDF pdf = PDF.of(distribution);
    Scalar p = pdf.at(mean.add(Quantity.of(3, "min")));
    Tolerance.CHOP.requireClose(p, Quantity.of(0.0011116453273056258, "s^-1"));
    CDF cdf = CDF.of(distribution);
    Scalar p_lessEquals = cdf.p_lessEquals(mean);
    Tolerance.CHOP.requireClose(RationalScalar.HALF, p_lessEquals);
    assertEquals(Mean.of(distribution), mean);
    assertEquals(StandardDeviation.of(distribution), sigma);
  }

  @Test
  void testDateTimeHour() {
    DateTime dateTime = DateTime.of(1090, 2, 3, 4, 5);
    Scalar sigma = Quantity.of(123, "h");
    Distribution distribution = NormalDistribution.of(dateTime, sigma);
    Scalar scalar = RandomVariate.of(distribution);
    assertInstanceOf(DateTime.class, scalar);
    ScalarUnaryOperator suo = UnitConvert.SI().to("h^2");
    assertEquals(suo.apply(Variance.of(distribution)), Quantity.of(15129, "h^2"));
    PDF pdf = PDF.of(distribution);
    Scalar p = pdf.at(dateTime.add(Quantity.of(3, "h")));
    Sign.requirePositive(p);
  }

  @Test
  void testVarZero() {
    Distribution distribution = NormalDistribution.of(3, 0);
    assertEquals(Mean.of(distribution), RealScalar.of(3));
    assertEquals(RandomVariate.of(distribution), RealScalar.of(3));
  }

  @Test
  void testMonotonous() {
    TestMarkovChebyshev.monotonous(NormalDistribution.of(-1, 3));
  }

  @Test
  void testComplexFail() {
    assertThrows(ClassCastException.class, () -> NormalDistribution.of(ComplexScalar.of(1, 2), RealScalar.ONE));
    assertThrows(ClassCastException.class, () -> NormalDistribution.of(RealScalar.ONE, ComplexScalar.of(1, 2)));
  }

  @Test
  void testQuantityFail() {
    assertThrows(Throw.class, () -> NormalDistribution.of(Quantity.of(3, "m"), Quantity.of(2, "km")));
    assertThrows(Throw.class, () -> NormalDistribution.of(Quantity.of(0, "s"), Quantity.of(2, "m")));
    assertThrows(Throw.class, () -> NormalDistribution.of(Quantity.of(0, ""), Quantity.of(2, "m")));
  }

  @Test
  void testNegativeSigmaFail() {
    NormalDistribution.of(5, 1);
    assertThrows(Throw.class, () -> NormalDistribution.of(5, -1));
  }
}
