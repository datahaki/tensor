// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.qty.DateTime;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Median;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;

class CauchyDistributionTest {
  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(CauchyDistribution.of(2, 3));
    PDF pdf = PDF.of(distribution);
    Tolerance.CHOP.requireClose(pdf.at(RealScalar.of(1.2)), RealScalar.of(0.09905909321072325));
    CDF cdf = CDF.of(distribution);
    Scalar x = RealScalar.of(3.4);
    Scalar p_lessEquals = cdf.p_lessEquals(x);
    Tolerance.CHOP.requireClose(p_lessEquals, RealScalar.of(0.6389827415450001));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    Scalar quantile = inverseCDF.quantile(p_lessEquals);
    Tolerance.CHOP.requireClose(quantile, x);
    assertEquals(distribution.toString(), "CauchyDistribution[2, 3]");
    TestMarkovChebyshev.symmetricAroundMean(distribution);
  }

  @Test
  void testMedian() {
    Distribution distribution = CauchyDistribution.of(2, 0.3);
    Scalar median = (Scalar) Median.of(RandomVariate.of(distribution, 100));
    Clips.interval(-2, 4).requireInside(median);
    assertTrue(distribution.toString().startsWith("CauchyDistribution["));
    assertEquals(Mean.of(distribution).toString(), "NaN");
    assertEquals(Variance.of(distribution).toString(), "NaN");
  }

  @Test
  void testDateTime() {
    Scalar a = DateTime.of(3000, 3, 4, 5, 6);
    Scalar b = Quantity.of(123, "s");
    Distribution distribution = CauchyDistribution.of(a, b);
    Scalar scalar = RandomVariate.of(distribution);
    assertInstanceOf(DateTime.class, scalar);
    PDF pdf = PDF.of(distribution);
    pdf.at(a);
    CDF cdf = CDF.of(distribution);
    Scalar p_lessEquals = cdf.p_lessEquals(a);
    assertEquals(RationalScalar.HALF, p_lessEquals);
  }

  @Test
  void testDateTimeHour() {
    Scalar a = DateTime.of(2020, 1, 1, 1, 1);
    Scalar b = Quantity.of(123, "h");
    Distribution distribution = CauchyDistribution.of(a, b);
    Scalar scalar = RandomVariate.of(distribution);
    assertInstanceOf(DateTime.class, scalar);
    PDF pdf = PDF.of(distribution);
    Scalar at = pdf.at(DateTime.of(2030, 1, 1, 1, 1));
    Sign.requirePositive(at);
    CDF cdf = CDF.of(distribution);
    Scalar p_lessEquals = cdf.p_lessEquals(a);
    assertEquals(RationalScalar.HALF, p_lessEquals);
  }

  @Test
  void testMonotonous() {
    TestMarkovChebyshev.monotonous(CauchyDistribution.of(0.3, 0.8));
  }

  @Test
  void testUnitFail() {
    assertThrows(Exception.class, () -> CauchyDistribution.of(Quantity.of(3, "s"), Quantity.of(3, "m")));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> CauchyDistribution.of(null, RealScalar.ONE));
    assertThrows(NullPointerException.class, () -> CauchyDistribution.of(RealScalar.ONE, null));
  }

  @Test
  void testZeroFail() {
    assertThrows(Throw.class, () -> CauchyDistribution.of(RealScalar.ONE, RealScalar.ZERO));
  }

  @Test
  void testNegativeFail() {
    assertThrows(Throw.class, () -> CauchyDistribution.of(RealScalar.ONE, RealScalar.of(-1)));
  }

  @Test
  void testStandardString() {
    assertEquals(CauchyDistribution.standard().toString(), "CauchyDistribution[0, 1]");
  }
}
