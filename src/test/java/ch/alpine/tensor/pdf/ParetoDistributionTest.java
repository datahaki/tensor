// code by jph
package ch.alpine.tensor.pdf;

import java.io.IOException;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.NumberQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ParetoDistributionTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(ParetoDistribution.of(RealScalar.of(2.3), RealScalar.of(1.8)));
    PDF pdf = PDF.of(distribution);
    Tolerance.CHOP.requireClose(pdf.at(RealScalar.of(4.0)), RealScalar.of(0.16619372965993448));
    Tolerance.CHOP.requireClose(pdf.at(RealScalar.of(2.3)), RealScalar.of(0.7826086956521743));
    Tolerance.CHOP.requireZero(pdf.at(RealScalar.of(2.2)));
    CDF cdf = CDF.of(distribution);
    Tolerance.CHOP.requireClose(cdf.p_lessEquals(RealScalar.of(4.0)), RealScalar.of(0.6306806007557013));
    Tolerance.CHOP.requireZero(cdf.p_lessEquals(RealScalar.of(2.3)));
    Tolerance.CHOP.requireZero(cdf.p_lessEquals(RealScalar.of(2.2)));
  }

  public void testMeanVariance() {
    Distribution distribution = ParetoDistribution.of(RealScalar.of(2.3), RealScalar.of(7.8));
    Scalar mean = Mean.of(distribution);
    Scalar varc = Variance.of(distribution);
    Tensor tensor = RandomVariate.of(distribution, 1000);
    Scalar empiricalMean = (Scalar) Mean.of(tensor);
    Scalar empiricalVarc = Variance.ofVector(tensor);
    Chop chop = Chop.below(0.3);
    chop.requireClose(mean, empiricalMean);
    chop.requireClose(varc, empiricalVarc);
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    Tolerance.CHOP.requireClose(inverseCDF.quantile(RealScalar.of(0.2)), RealScalar.of(2.366748969310483));
    assertEquals(inverseCDF.quantile(RealScalar.ZERO), RealScalar.of(2.3));
    assertEquals(inverseCDF.quantile(RealScalar.ONE), DoubleScalar.POSITIVE_INFINITY);
    assertTrue(distribution.toString().startsWith("ParetoDistribution["));
  }

  public void testMeanVarianceIndeterminate() {
    Distribution distribution = ParetoDistribution.of(RealScalar.of(2.3), RealScalar.of(1));
    assertFalse(NumberQ.of(Mean.of(distribution)));
    assertFalse(NumberQ.of(Variance.of(distribution)));
  }

  public void testNegativeFail() {
    AssertFail.of(() -> ParetoDistribution.of(RealScalar.of(2.3), RealScalar.of(0)));
    AssertFail.of(() -> ParetoDistribution.of(RealScalar.of(0), RealScalar.of(3)));
  }

  public void testQuantityFail() {
    AssertFail.of(() -> ParetoDistribution.of(RealScalar.of(3.3), Quantity.of(2.3, "m")));
  }

  public void testKQuantityFail() {
    AssertFail.of(() -> ParetoDistribution.of(Quantity.of(2.3, "m"), RealScalar.of(3.3)));
  }
}
