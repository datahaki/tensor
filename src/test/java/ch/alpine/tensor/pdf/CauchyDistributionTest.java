// code by jph
package ch.alpine.tensor.pdf;

import java.io.IOException;

import ch.alpine.tensor.DeterminateScalarQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Median;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.usr.AssertFail;
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
    Scalar median = (Scalar) Median.of(RandomVariate.of(distribution, 100));
    Clips.interval(-2, 4).requireInside(median);
    assertTrue(distribution.toString().startsWith("CauchyDistribution["));
    assertFalse(DeterminateScalarQ.of(Mean.of(distribution)));
    assertFalse(DeterminateScalarQ.of(Variance.of(distribution)));
  }

  public void testNullFail() {
    AssertFail.of(() -> CauchyDistribution.of(null, RealScalar.ONE));
    AssertFail.of(() -> CauchyDistribution.of(RealScalar.ONE, null));
  }

  public void testZeroFail() {
    AssertFail.of(() -> CauchyDistribution.of(RealScalar.ONE, RealScalar.ZERO));
  }

  public void testNegativeFail() {
    AssertFail.of(() -> CauchyDistribution.of(RealScalar.ONE, RealScalar.of(-1)));
  }

  public void testStandardString() {
    assertEquals(CauchyDistribution.standard().toString(), "CauchyDistribution[0, 1]");
  }
}
