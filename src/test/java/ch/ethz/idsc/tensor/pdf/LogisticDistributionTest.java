// code by jph
package ch.ethz.idsc.tensor.pdf;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.mat.Tolerance;
import junit.framework.TestCase;

public class LogisticDistributionTest extends TestCase {
  public void testSimple() {
    Distribution distribution = LogisticDistribution.of(2, 3);
    CDF cdf = CDF.of(distribution);
    Scalar x = RealScalar.of(3.4);
    Scalar p_lessEquals = cdf.p_lessEquals(x);
    Tolerance.CHOP.requireClose(p_lessEquals, RealScalar.of(0.6145944982675495));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    Scalar quantile = inverseCDF.quantile(p_lessEquals);
    Tolerance.CHOP.requireClose(quantile, x);
  }

  public void testNullFail() {
    try {
      LogisticDistribution.of(null, RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      LogisticDistribution.of(RealScalar.ONE, null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testZeroFail() {
    try {
      LogisticDistribution.of(RealScalar.ONE, RealScalar.ZERO);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testNegativeFail() {
    try {
      LogisticDistribution.of(RealScalar.ONE, RealScalar.of(-1));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
