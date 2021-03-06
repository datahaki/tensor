// code by jph
package ch.alpine.tensor.pdf;

import ch.alpine.tensor.DeterminateScalarQ;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.NumberQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.qty.UnitConvert;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class GumbelDistributionTest extends TestCase {
  public void testPDF() {
    Distribution distribution = //
        GumbelDistribution.of(RealScalar.of(3), RealScalar.of(0.2));
    PDF pdf = PDF.of(distribution);
    Scalar prob = pdf.at(RealScalar.of(2.9));
    Chop._13.requireClose(prob, RealScalar.of(1.65352149445209));
    assertEquals(pdf.at(RealScalar.of(4.5)), RealScalar.ZERO);
  }

  public void testCDF() {
    Distribution distribution = //
        GumbelDistribution.of(RealScalar.of(3), RealScalar.of(0.2));
    CDF cdf = CDF.of(distribution);
    Scalar prob = cdf.p_lessEquals(RealScalar.of(2.9));
    Chop._13.requireClose(prob, RealScalar.of(0.45476078810739484));
    assertEquals(cdf.p_lessEquals(RealScalar.of(4)), RealScalar.ONE);
  }

  public void testRandomVariate() {
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
    DeterminateScalarQ.require(gmd.protected_quantile(RealScalar.of(Math.nextDown(1.0))));
  }

  public void testQuantity() {
    Distribution distribution = GumbelDistribution.of(Quantity.of(0.3, "m^-1"), Quantity.of(0.4, "m^-1"));
    Scalar rand = RandomVariate.of(distribution);
    assertTrue(rand instanceof Quantity);
    UnitConvert.SI().to(Unit.of("in^-1")).apply(rand);
    {
      Scalar prob = PDF.of(distribution).at(Quantity.of(1, "m^-1"));
      QuantityMagnitude.SI().in(Unit.of("in")).apply(prob);
    }
    {
      CDF cdf = CDF.of(distribution);
      Scalar prob = cdf.p_lessEquals(Quantity.of(10, "m^-1"));
      assertTrue(prob instanceof DoubleScalar);
      assertTrue(NumberQ.of(prob));
    }
  }

  public void testMean() {
    Distribution distribution = //
        GumbelDistribution.of(Quantity.of(-0.3, "m^-1"), Quantity.of(0.4, "m^-1"));
    Scalar mean = Expectation.mean(distribution);
    Chop._13.requireClose(mean, Quantity.of(-0.5308862659606132, "m^-1"));
  }

  public void testVariance() {
    Distribution distribution = //
        GumbelDistribution.of(Quantity.of(-1.3, "m^-1"), Quantity.of(1.5, "m^-1"));
    Scalar var = Expectation.variance(distribution);
    Chop._13.requireClose(var, Quantity.of(3.7011016504085092, "m^-2"));
  }

  public void testToString() {
    Distribution distribution = //
        GumbelDistribution.of(RealScalar.of(3), RealScalar.of(0.2));
    assertEquals(distribution.toString(), "GumbelDistribution[3, 0.2]");
  }

  public void testBetaNonPositiveFail() {
    AssertFail.of(() -> GumbelDistribution.of(RealScalar.of(3), RealScalar.of(0)));
    AssertFail.of(() -> GumbelDistribution.of(RealScalar.of(3), RealScalar.of(-1)));
  }
}
