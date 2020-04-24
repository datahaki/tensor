// code by jph
package ch.ethz.idsc.tensor.pdf;

import java.io.IOException;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.red.Median;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class CauchyDistributionTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
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

  public void testMedian() {
    Distribution distribution = CauchyDistribution.of(2, 0.3);
    Scalar median = Median.of(RandomVariate.of(distribution, 100)).Get();
    Clips.interval(-2, 4).requireInside(median);
    assertTrue(distribution.toString().startsWith("CauchyDistribution["));
  }

  public void testNullFail() {
    try {
      CauchyDistribution.of(null, RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      CauchyDistribution.of(RealScalar.ONE, null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testZeroFail() {
    try {
      CauchyDistribution.of(RealScalar.ONE, RealScalar.ZERO);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testNegativeFail() {
    try {
      CauchyDistribution.of(RealScalar.ONE, RealScalar.of(-1));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
