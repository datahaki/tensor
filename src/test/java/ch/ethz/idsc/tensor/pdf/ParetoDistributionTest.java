// code by jph
package ch.ethz.idsc.tensor.pdf;

import java.io.IOException;

import ch.ethz.idsc.tensor.NumberQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Variance;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ParetoDistributionTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(ParetoDistribution.of(RealScalar.of(2.3), RealScalar.of(1.8)));
    PDF pdf = PDF.of(distribution);
    Chop._10.requireClose(pdf.at(RealScalar.of(4.0)), RealScalar.of(0.16619372965993448));
    Chop._10.requireClose(pdf.at(RealScalar.of(2.3)), RealScalar.of(0.7826086956521743));
    Chop._10.requireZero(pdf.at(RealScalar.of(2.2)));
    CDF cdf = CDF.of(distribution);
    Chop._10.requireClose(cdf.p_lessEquals(RealScalar.of(4.0)), RealScalar.of(0.6306806007557013));
    Chop._10.requireZero(cdf.p_lessEquals(RealScalar.of(2.3)));
    Chop._10.requireZero(cdf.p_lessEquals(RealScalar.of(2.2)));
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
