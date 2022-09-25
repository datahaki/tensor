// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.jet.DateObject;
import ch.alpine.tensor.mat.Tolerance;
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
import ch.alpine.tensor.red.CentralMoment;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;

class GumbelDistributionTest {
  @Test
  void testPDF() {
    Distribution distribution = //
        GumbelDistribution.of(RealScalar.of(3), RealScalar.of(0.2));
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
    FiniteScalarQ.require(gmd.protected_quantile(RealScalar.of(Math.nextDown(1.0))));
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
  void testDateTimeScalar() {
    DateObject dateTimeScalar = DateObject.of(LocalDateTime.now());
    Distribution distribution = GumbelDistribution.of(dateTimeScalar, Quantity.of(123, "s"));
    Scalar scalar = RandomVariate.of(distribution);
    assertInstanceOf(DateObject.class, scalar);
    PDF pdf = PDF.of(distribution);
    pdf.at(DateObject.of(LocalDateTime.now()));
    CDF cdf = CDF.of(distribution);
    Scalar p_lessEquals = cdf.p_lessEquals(DateObject.of(LocalDateTime.now()));
    Clips.interval(0.5, 0.8).requireInside(p_lessEquals);
    RandomVariate.of(distribution, 10);
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
