// code by jph
package ch.ethz.idsc.tensor.pdf;

import java.io.IOException;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Variance;
import junit.framework.TestCase;

public class LogisticDistributionTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(LogisticDistribution.of(2, 3));
    PDF pdf = PDF.of(distribution);
    Tolerance.CHOP.requireClose(pdf.at(RealScalar.of(1.2)), RealScalar.of(0.0818692348913425));
    CDF cdf = CDF.of(distribution);
    Scalar x = RealScalar.of(3.4);
    Scalar p_lessEquals = cdf.p_lessEquals(x);
    Tolerance.CHOP.requireClose(p_lessEquals, RealScalar.of(0.6145944982675495));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    Scalar quantile = inverseCDF.quantile(p_lessEquals);
    Tolerance.CHOP.requireClose(quantile, x);
  }

  public void testRandomMeanVar() {
    Distribution distribution = LogisticDistribution.of(3, 2);
    RandomVariate.of(distribution, 100);
    Tolerance.CHOP.requireClose(ExactScalarQ.require(Mean.of(distribution)), RealScalar.of(3));
    Tolerance.CHOP.requireClose(Variance.of(distribution), RealScalar.of(13.15947253478581));
    assertEquals(distribution.toString(), "LogisticDistribution[3, 2]");
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
