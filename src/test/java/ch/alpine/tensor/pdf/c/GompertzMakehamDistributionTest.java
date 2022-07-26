// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.qty.UnitConvert;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;

class GompertzMakehamDistributionTest {
  @Test
  void testPDF() throws ClassNotFoundException, IOException {
    Distribution distribution = //
        Serialization.copy(GompertzMakehamDistribution.of(RealScalar.of(3), RealScalar.of(0.2)));
    PDF pdf = PDF.of(distribution);
    Scalar prob = pdf.at(RealScalar.of(0.35));
    Chop._13.requireClose(prob, RealScalar.of(1.182515740643019));
    assertEquals(pdf.at(RealScalar.of(4.35)), RealScalar.ZERO);
  }

  @Test
  void testCDF() {
    Distribution distribution = //
        GompertzMakehamDistribution.of(RealScalar.of(3), RealScalar.of(0.2));
    CDF cdf = CDF.of(distribution);
    Scalar prob = cdf.p_lessEquals(RealScalar.of(0.35));
    Chop._13.requireClose(prob, RealScalar.of(0.3103218390514517));
    assertEquals(cdf.p_lessEquals(RealScalar.of(4.35)), RealScalar.ONE);
    assertEquals(CDF.of(distribution).p_lessThan(RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(CDF.of(distribution).p_lessEquals(RealScalar.ZERO), RealScalar.ZERO);
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    Tolerance.CHOP.requireClose(inverseCDF.quantile(RealScalar.of(0.75)), RealScalar.of(0.6902795393741822));
  }

  @Test
  void testRandomVariate() {
    GompertzMakehamDistribution gmd = (GompertzMakehamDistribution) //
    GompertzMakehamDistribution.of(RealScalar.of(3), RealScalar.of(0.2));
    assertTrue(Scalars.isZero(gmd.protected_quantile(RealScalar.ZERO)));
    assertTrue(Scalars.lessThan(gmd.protected_quantile(RealScalar.ZERO), RealScalar.of(3)));
    Scalar scalar = gmd.protected_quantile(RealScalar.of(Math.nextDown(1.0)));
    Clips.interval(1.7, 2).requireInside(scalar);
  }

  @Test
  void testQuantity() {
    Distribution distribution = GompertzMakehamDistribution.of(Quantity.of(0.3, "m^-1"), RealScalar.of(0.1));
    Scalar rand = RandomVariate.of(distribution);
    assertInstanceOf(Quantity.class, rand);
    UnitConvert.SI().to(Unit.of("in")).apply(rand);
    {
      Scalar prob = PDF.of(distribution).at(Quantity.of(1, "m"));
      QuantityMagnitude.SI().in(Unit.of("in^-1")).apply(prob);
    }
    {
      CDF cdf = CDF.of(distribution);
      Scalar prob = cdf.p_lessEquals(Quantity.of(10, "m"));
      assertInstanceOf(DoubleScalar.class, prob);
      assertTrue(FiniteScalarQ.of(prob));
    }
  }

  @Test
  void testQuantityPDF() {
    Distribution distribution = GompertzMakehamDistribution.of(Quantity.of(0.3, "m^-1"), RealScalar.of(0.1));
    {
      Scalar prob = PDF.of(distribution).at(Quantity.of(-1, "m"));
      assertInstanceOf(Quantity.class, prob);
      assertTrue(Scalars.isZero(prob));
      QuantityMagnitude.SI().in(Unit.of("in^-1")).apply(prob);
    }
    assertEquals(CDF.of(distribution).p_lessThan(Quantity.of(-2, "m^1*s^0")), RealScalar.ZERO);
  }

  @Test
  void testVarianceFail() {
    GompertzMakehamDistribution distribution = //
        (GompertzMakehamDistribution) GompertzMakehamDistribution.of(Quantity.of(0.3, "m^-1"), RealScalar.of(0.1));
    assertThrows(UnsupportedOperationException.class, () -> distribution.variance());
  }

  @Test
  void testToString() {
    Distribution distribution = GompertzMakehamDistribution.of(Quantity.of(0.3, "m^-1"), RealScalar.of(0.1));
    String string = distribution.toString();
    assertTrue(string.startsWith(GompertzMakehamDistribution.class.getSimpleName()));
  }

  @Test
  void testPdfUnitFail() {
    Distribution distribution = GompertzMakehamDistribution.of(Quantity.of(0.3, "m^-1"), RealScalar.of(0.1));
    PDF pdf = PDF.of(distribution);
    assertEquals(pdf.at(Quantity.of(0.0, "m")), Quantity.of(0.03, "m^-1"));
    assertThrows(Throw.class, () -> pdf.at(Quantity.of(-1, "m^2")));
    assertThrows(Throw.class, () -> pdf.at(Quantity.of(+1, "m^2")));
  }

  @Test
  void testCdfUnitFail() {
    Distribution distribution = GompertzMakehamDistribution.of(Quantity.of(0.3, "m^-1"), RealScalar.of(0.1));
    CDF cdf = CDF.of(distribution);
    Tolerance.CHOP.requireClose(cdf.p_lessEquals(Quantity.of(+0.1, "m")), RealScalar.of(0.003040820706232905));
    assertEquals(cdf.p_lessEquals(Quantity.of(+0.0, "m")), RealScalar.ZERO);
    assertEquals(cdf.p_lessEquals(Quantity.of(-0.1, "m")), RealScalar.ZERO);
    assertThrows(Throw.class, () -> cdf.p_lessEquals(Quantity.of(-1, "m^2")));
    assertThrows(Throw.class, () -> cdf.p_lessEquals(Quantity.of(+1, "m^2")));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    Scalar quantile = inverseCDF.quantile(RationalScalar.of(1, 8));
    Tolerance.CHOP.requireClose(quantile, Scalars.fromString("2.8271544195740326[m]"));
  }

  @Test
  void testInverseCDFFail() {
    Distribution distribution = //
        GompertzMakehamDistribution.of(RealScalar.of(3), RealScalar.of(0.2));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    assertThrows(Throw.class, () -> inverseCDF.quantile(RealScalar.of(-0.1)));
    assertThrows(Throw.class, () -> inverseCDF.quantile(RealScalar.of(+1.1)));
  }

  @Test
  void testFail() {
    assertThrows(Throw.class, () -> GompertzMakehamDistribution.of(RealScalar.of(0), RealScalar.of(0.2)));
    assertThrows(Throw.class, () -> GompertzMakehamDistribution.of(RealScalar.of(3), RealScalar.of(0)));
    assertThrows(Throw.class, () -> GompertzMakehamDistribution.of(RealScalar.of(1e-300), RealScalar.of(1e-300)));
  }
}
