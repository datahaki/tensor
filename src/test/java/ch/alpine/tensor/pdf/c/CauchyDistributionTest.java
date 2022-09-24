// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.jet.DateObject;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Median;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;

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
  void testDateTimeScalar() {
    DateObject dateTimeScalar = DateObject.of(LocalDateTime.now());
    Scalar durationScalar = Quantity.of(123, "s");
    Distribution distribution = CauchyDistribution.of(dateTimeScalar, durationScalar);
    Scalar scalar = RandomVariate.of(distribution);
    assertInstanceOf(DateObject.class, scalar);
    PDF pdf = PDF.of(distribution);
    pdf.at(DateObject.of(LocalDateTime.now()));
    CDF cdf = CDF.of(distribution);
    Scalar p_lessEquals = cdf.p_lessEquals(DateObject.of(LocalDateTime.now()));
    Chop._01.requireClose(RationalScalar.HALF, p_lessEquals);
  }

  @Test
  void testMonotonous() {
    TestMarkovChebyshev.monotonous(CauchyDistribution.of(0.3, 0.8));
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
