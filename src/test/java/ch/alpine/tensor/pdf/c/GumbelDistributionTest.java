// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Month;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.Expectation;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.pdf.UnivariateDistribution;
import ch.alpine.tensor.qty.DateTime;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.qty.UnitConvert;
import ch.alpine.tensor.red.CentralMoment;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;

class GumbelDistributionTest {
  @Test
  void testPDF() {
    Distribution distribution = GumbelDistribution.of(3, 0.2);
    PDF pdf = PDF.of(distribution);
    Scalar prob = pdf.at(RealScalar.of(2.9));
    Chop._13.requireClose(prob, RealScalar.of(1.65352149445209));
    assertEquals(pdf.at(RealScalar.of(4.5)), RealScalar.ZERO);
  }

  @Test
  void testCDF() {
    Distribution distribution = //
        GumbelDistribution.of(RealScalar.of(3), RealScalar.of(0.2));
    CDF cdf = CDF.of(distribution);
    Scalar prob = cdf.p_lessEquals(RealScalar.of(2.9));
    Chop._13.requireClose(prob, RealScalar.of(0.45476078810739484));
    assertEquals(cdf.p_lessEquals(RealScalar.of(4)), RealScalar.ONE);
  }

  @Test
  void testRandomVariate() {
    GumbelDistribution gmd = (GumbelDistribution) //
    GumbelDistribution.of(RealScalar.of(3), RealScalar.of(0.2));
    assertTrue(Scalars.lessThan(gmd.protected_quantile(RealScalar.ZERO), RealScalar.of(4.5)));
    assertTrue(Scalars.lessThan(RealScalar.of(-4.5), gmd.protected_quantile(RealScalar.of(Math.nextDown(1.0)))));
    InverseCDF inverseCDF = InverseCDF.of(gmd);
    Tolerance.CHOP.requireClose( //
        inverseCDF.quantile(RealScalar.of(0.123)), //
        RealScalar.of(2.5938671136008074));
    assertEquals(inverseCDF.quantile(RealScalar.ZERO), DoubleScalar.NEGATIVE_INFINITY);
    assertEquals(inverseCDF.quantile(RealScalar.ONE), DoubleScalar.POSITIVE_INFINITY);
    // System.out.println(gmd.randomVariate(0.0));
    assertTrue(FiniteScalarQ.of(gmd.protected_quantile(RealScalar.of(Math.nextDown(1.0)))));
  }

  @Test
  void testQuantity() {
    Distribution distribution = GumbelDistribution.of(Quantity.of(0.3, "m^-1"), Quantity.of(0.4, "m^-1"));
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
    UnivariateDistribution ud = (UnivariateDistribution) distribution;
    assertEquals(ud.support(), Clips.absolute(Quantity.of(Double.POSITIVE_INFINITY, "m^-1")));
  }

  @Test
  void testMean() {
    Distribution distribution = //
        GumbelDistribution.of(Quantity.of(-0.3, "m^-1"), Quantity.of(0.4, "m^-1"));
    Scalar mean = Expectation.mean(distribution);
    Chop._13.requireClose(mean, Quantity.of(-0.5308862659606132, "m^-1"));
  }

  @Test
  void testVariance() {
    Distribution distribution = //
        GumbelDistribution.of(Quantity.of(-1.3, "m^-1"), Quantity.of(1.5, "m^-1"));
    Scalar var = Expectation.variance(distribution);
    Chop._13.requireClose(var, Quantity.of(3.7011016504085092, "m^-2"));
  }

  @Test
  void testCentralMoment() {
    Distribution distribution = //
        GumbelDistribution.of(Quantity.of(-1.3, "m^-1"), Quantity.of(1.5, "m^-1"));
    assertEquals(CentralMoment.of(distribution, 0), RealScalar.ONE);
    assertEquals(CentralMoment.of(distribution, 1), Quantity.of(0, "m^-1"));
    Scalar var = CentralMoment.of(distribution, 2);
    Chop._13.requireClose(var, Quantity.of(3.7011016504085092, "m^-2"));
    assertThrows(Exception.class, () -> CentralMoment.of(distribution, 3));
  }

  @Test
  void testToString() {
    Distribution distribution = //
        GumbelDistribution.of(RealScalar.of(3), RealScalar.of(0.2));
    assertEquals(distribution.toString(), "GumbelDistribution[3, 0.2]");
  }

  @Test
  void testDateTime() {
    DateTime alpha = DateTime.of(1978, 4, 5, 6, 7);
    Distribution distribution = GumbelDistribution.of(alpha, Quantity.of(123, "s"));
    Scalar scalar = RandomVariate.of(distribution);
    assertInstanceOf(DateTime.class, scalar);
    PDF pdf = PDF.of(distribution);
    Scalar p_at = pdf.at(alpha);
    Tolerance.CHOP.requireClose(p_at, Quantity.of(0.0029908897656214825, "s^-1"));
    CDF cdf = CDF.of(distribution);
    Scalar p_lessEquals = cdf.p_lessEquals(alpha);
    Tolerance.CHOP.requireClose(p_lessEquals, RealScalar.of(0.6321205588285577));
  }

  @Test
  void testDateTimeHour() {
    DateTime alpha = DateTime.of(2020, Month.AUGUST, 19, 18, 17, 16);
    Scalar sigma = Quantity.of(2, "h");
    Distribution distribution = GumbelDistribution.of(alpha, sigma);
    Scalar scalar = RandomVariate.of(distribution);
    assertInstanceOf(DateTime.class, scalar);
    assertEquals(Mean.of(distribution), DateTime.parse("2020-08-19T17:08:00.047212708"));
    ScalarUnaryOperator suo = UnitConvert.SI().to("h^2");
    Tolerance.CHOP.requireClose(suo.apply(Variance.of(distribution)), Quantity.of(6.579736267392906, "h^2"));
    PDF pdf = PDF.of(distribution);
    Scalar p = pdf.at(DateTime.of(2020, Month.AUGUST, 19, 18, 27, 17));
    Tolerance.CHOP.requireClose(p, Quantity.of(5.0911633644993024E-5, "s^-1"));
    CDF cdf = CDF.of(distribution);
    DateTime x = DateTime.of(2020, Month.SEPTEMBER, 01, 0, 0, 0);
    Scalar p_lessEquals = cdf.p_lessEquals(x);
    Tolerance.CHOP.requireClose(p_lessEquals, RealScalar.ONE);
  }

  @Test
  void testDateTimeDays() {
    DateTime alpha = DateTime.of(2020, Month.AUGUST, 19, 18, 17, 16);
    Scalar sigma = Quantity.of(20, "days");
    Distribution distribution = GumbelDistribution.of(alpha, sigma);
    Scalar scalar = RandomVariate.of(distribution);
    assertInstanceOf(DateTime.class, scalar);
    assertEquals(Mean.of(distribution), DateTime.parse("2020-08-08T05:13:27.331050151"));
    PDF pdf = PDF.of(distribution);
    Scalar p = pdf.at(DateTime.of(2020, Month.AUGUST, 19, 18, 27, 17));
    Tolerance.CHOP.requireClose(p, Quantity.of(2.128931822445057E-7, "s^-1"));
    CDF cdf = CDF.of(distribution);
    DateTime x = DateTime.of(2020, Month.SEPTEMBER, 03, 5, 6, 7);
    Scalar p_lessEquals = cdf.p_lessEquals(x);
    Tolerance.CHOP.requireClose(p_lessEquals, RealScalar.of(0.8724996929452071));
  }

  @Test
  void testMonotonous() {
    TestMarkovChebyshev.monotonous(GumbelDistribution.of(1.2, 2.2));
  }

  @Test
  void testBetaNonPositiveFail() {
    assertThrows(Throw.class, () -> GumbelDistribution.of(RealScalar.of(3), RealScalar.of(0)));
    assertThrows(Throw.class, () -> GumbelDistribution.of(RealScalar.of(3), RealScalar.of(-1)));
  }

  @Test
  void testComplexFail() {
    assertThrows(ClassCastException.class, () -> GumbelDistribution.of(ComplexScalar.of(1, 2), RealScalar.ONE));
    assertThrows(ClassCastException.class, () -> GumbelDistribution.of(RealScalar.ONE, ComplexScalar.of(1, 2)));
  }

  @Test
  void testQuantityFail() {
    assertThrows(Throw.class, () -> GumbelDistribution.of(Quantity.of(3, "m"), Quantity.of(2, "km")));
    assertThrows(Throw.class, () -> GumbelDistribution.of(Quantity.of(0, "s"), Quantity.of(2, "m")));
    assertThrows(Throw.class, () -> GumbelDistribution.of(Quantity.of(0, ""), Quantity.of(2, "m")));
  }
}
