// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.jet.DateObject;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.Expectation;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.pdf.d.BinomialDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.red.CentralMoment;
import ch.alpine.tensor.red.Kurtosis;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.StandardDeviation;
import ch.alpine.tensor.sca.Chop;

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
  }

  @Test
  void testToString() {
    Distribution distribution = NormalDistribution.of(Quantity.of(3, "m"), Quantity.of(2, "m"));
    String string = distribution.toString();
    assertEquals(string, "NormalDistribution[3[m], 2[m]]");
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
  void testDateTimeScalar() {
    DateObject dateTimeScalar = DateObject.of(LocalDateTime.now());
    Scalar durationScalar = Quantity.of(123, "s");
    Distribution distribution = NormalDistribution.of(dateTimeScalar, durationScalar);
    Scalar scalar = RandomVariate.of(distribution);
    assertInstanceOf(DateObject.class, scalar);
    PDF pdf = PDF.of(distribution);
    pdf.at(DateObject.of(LocalDateTime.now()));
    CDF cdf = CDF.of(distribution);
    Scalar p_lessEquals = cdf.p_lessEquals(DateObject.of(LocalDateTime.now()));
    Chop._01.requireClose(RationalScalar.HALF, p_lessEquals);
    assertEquals(Mean.of(distribution), dateTimeScalar);
    assertEquals(StandardDeviation.of(distribution), durationScalar);
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
