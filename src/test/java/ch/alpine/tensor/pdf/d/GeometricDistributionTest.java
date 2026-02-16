// code by jph
package ch.alpine.tensor.pdf.d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.Expectation;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;

class GeometricDistributionTest {
  @Test
  void testPdf() {
    PDF pdf = PDF.of(GeometricDistribution.of(Rational.of(1, 3)));
    assertEquals(pdf.at(RealScalar.ZERO), Rational.of(1, 3));
    assertEquals(pdf.at(RealScalar.of(1)), Rational.of(2, 9));
    assertEquals(pdf.at(RealScalar.of(2)), Rational.of(4, 27));
    assertEquals(pdf.at(RealScalar.of(1)), Rational.of(1, 3).multiply(Rational.of(2, 3)));
    assertEquals(pdf.at(RealScalar.of(2)), Rational.of(1, 3).multiply(Rational.of(4, 9)));
  }

  @Test
  void testNarrow() throws ClassNotFoundException, IOException {
    final Scalar p = Rational.of(1, 19);
    GeometricDistribution distribution = //
        (GeometricDistribution) Serialization.copy(GeometricDistribution.of(p));
    PDF pdf = PDF.of(distribution);
    Scalar peq0 = pdf.at(RealScalar.ZERO);
    Scalar peq1 = pdf.at(RealScalar.ONE);
    Scalar plt2 = peq0.add(peq1);
    assertEquals(peq0, p);
    CDF cdf = CDF.of(distribution);
    assertEquals(cdf.p_lessThan(RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(cdf.p_lessEquals(RealScalar.ZERO), p);
    assertEquals(cdf.p_lessThan(RealScalar.of(0.1)), p);
    assertEquals(cdf.p_lessEquals(RealScalar.of(0.1)), p);
    assertEquals(cdf.p_lessThan(RealScalar.ONE), p);
    assertEquals(cdf.p_lessThan(RealScalar.of(1.1)), plt2);
    assertEquals(cdf.p_lessEquals(RealScalar.ONE), plt2);
    assertEquals(cdf.p_lessEquals(RealScalar.of(1.1)), plt2);
    Scalar large = cdf.p_lessEquals(RealScalar.of(100.1));
    ExactScalarQ.require(large);
  }

  @Test
  void testFailP() {
    assertThrows(Throw.class, () -> GeometricDistribution.of(RealScalar.ZERO));
    assertThrows(Throw.class, () -> GeometricDistribution.of(RealScalar.of(1.1)));
  }

  @Test
  void testOne() {
    Distribution distribution = GeometricDistribution.of(RealScalar.ONE);
    assertEquals(PDF.of(distribution).at(RealScalar.ZERO), RealScalar.ONE);
    assertEquals(PDF.of(distribution).at(RealScalar.ONE), RealScalar.ZERO);
    assertEquals(RandomVariate.of(distribution), RealScalar.ZERO);
    assertEquals(Expectation.mean(distribution), RealScalar.ZERO);
    assertEquals(Expectation.variance(distribution), RealScalar.ZERO);
  }

  @Test
  void testNumerics() {
    Distribution distribution = GeometricDistribution.of(RealScalar.of(0.002));
    CDF cdf = CDF.of(distribution);
    Scalar s = cdf.p_lessEquals(RealScalar.of(1000000000));
    assertEquals(s, RealScalar.ONE);
  }

  @Test
  void testOutside() {
    PDF pdf = PDF.of(GeometricDistribution.of(Rational.of(1, 3)));
    assertEquals(pdf.at(RealScalar.of(-1)), RealScalar.ZERO);
  }

  @Test
  void testInverseCDF() {
    Distribution distribution = GeometricDistribution.of(Rational.of(1, 3));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    assertEquals(inverseCDF.quantile(DoubleScalar.of(1)), DoubleScalar.POSITIVE_INFINITY);
    assertEquals(inverseCDF.quantile(RealScalar.ONE), DoubleScalar.POSITIVE_INFINITY);
  }

  @Test
  void testToString() {
    Distribution distribution = GeometricDistribution.of(Rational.of(1, 3));
    assertEquals(distribution.toString(), "GeometricDistribution[1/3]");
  }

  @Test
  void testRandomVariate() {
    double P = 0.9999;
    AbstractDiscreteDistribution distribution = //
        (AbstractDiscreteDistribution) GeometricDistribution.of(RealScalar.of(P));
    {
      Scalar s = distribution.quantile(RealScalar.of(Math.nextDown(P)));
      assertEquals(s, RealScalar.ZERO);
    }
    {
      Scalar s = distribution.quantile(RealScalar.of(P));
      assertEquals(s, RealScalar.ONE);
    }
    {
      Scalar s = distribution.quantile(RealScalar.of(Math.nextDown(1.0)));
      assertEquals(s, RealScalar.of(3));
    }
  }

  @Test
  void testFailQuantile() {
    Distribution distribution = GeometricDistribution.of(RealScalar.of(0.2));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    assertThrows(Throw.class, () -> inverseCDF.quantile(RealScalar.of(-0.1)));
    assertThrows(Throw.class, () -> inverseCDF.quantile(RealScalar.of(1.1)));
  }

  @Test
  void testNextDownOne() {
    for (int c = 500; c <= 700; c += 100) {
      Scalar p = DoubleScalar.of(0.1 / c);
      AbstractDiscreteDistribution distribution = //
          (AbstractDiscreteDistribution) GeometricDistribution.of(p);
      Scalar scalar = distribution.quantile(RealScalar.of(Math.nextDown(1.0)));
      assertTrue(Scalars.lessThan(scalar, RealScalar.of(260000)));
    }
  }

  @Test
  void testQuantity() {
    assertThrows(Throw.class, () -> GeometricDistribution.of(Quantity.of(2, "s")));
    final Scalar p = Rational.of(1, 19);
    GeometricDistribution distribution = (GeometricDistribution) GeometricDistribution.of(p);
    try {
      distribution.at(Quantity.of(-2, "s")); // for now this returns 0
      fail();
    } catch (Exception exception) {
      // ---
    }
    assertThrows(Throw.class, () -> CDF.of(distribution).p_lessEquals(Quantity.of(-2, "s")));
  }

  private static void _checkCDFNumerics(Distribution distribution) {
    CDF cdf = CDF.of(distribution);
    assertEquals(cdf.p_lessEquals(RealScalar.of(-10)), RealScalar.ZERO);
    Scalar top = cdf.p_lessEquals(RealScalar.of(1000000));
    Chop._14.requireClose(top, RealScalar.ONE);
  }

  @Test
  void testNumericsGeometric() {
    _checkCDFNumerics(GeometricDistribution.of(0.01));
    _checkCDFNumerics(GeometricDistribution.of(0.1));
    _checkCDFNumerics(GeometricDistribution.of(0.9));
    _checkCDFNumerics(GeometricDistribution.of(0.99));
  }

  @Test
  void testMonotonous() {
    TestMarkovChebyshev.monotonous(GeometricDistribution.of(0.9));
  }
}
