// code by jph
package ch.alpine.tensor.pdf;

import java.io.IOException;

import ch.alpine.tensor.DeterminateScalarQ;
import ch.alpine.tensor.MachineNumberQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class DagumDistributionTest extends TestCase {
  public void testCdf() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(DagumDistribution.of(0.2, 0.3, 0.6));
    CDF cdf = CDF.of(distribution);
    Scalar scalar = cdf.p_lessThan(RealScalar.of(3));
    Chop._12.requireClose(scalar, RealScalar.of(0.9083561837802137));
  }

  public void testPdf() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(DagumDistribution.of(0.2, 0.3, 0.6));
    PDF pdf = PDF.of(distribution);
    Scalar scalar = pdf.at(RealScalar.of(2));
    Chop._12.requireClose(scalar, RealScalar.of(0.011083755340304258));
  }

  public void testInverseCdf() {
    Distribution distribution = DagumDistribution.of(0.2, 0.3, 0.6);
    InverseCDF cdf = InverseCDF.of(distribution);
    Scalar scalar = cdf.quantile(RealScalar.of(0.75));
    Chop._12.requireClose(scalar, RealScalar.of(0.012246219782933493));
  }

  public void testMean() {
    Distribution distribution = DagumDistribution.of(2.3, 1.2, 0.7);
    Scalar scalar = Mean.of(distribution);
    Chop._12.requireClose(scalar, RealScalar.of(7.579940034748095));
  }

  public void testVarianceFail() {
    DagumDistribution distribution = (DagumDistribution) DagumDistribution.of(2.3, 1.2, 0.7);
    AssertFail.of(() -> distribution.variance());
  }

  public void testMeanIndeterminate() {
    Distribution distribution = DagumDistribution.of(2.3, 1, 0.7);
    Scalar scalar = Mean.of(distribution);
    assertFalse(MachineNumberQ.of(scalar));
    assertFalse(DeterminateScalarQ.of(scalar));
  }

  public void testRandom() {
    Distribution distribution = DagumDistribution.of(0.2, 0.3, 0.6);
    Scalar mean = (Scalar) Mean.of(RandomVariate.of(distribution, 100));
    Sign.requirePositive(mean);
  }

  public void testString() {
    Distribution distribution = DagumDistribution.of(1, 2, 3);
    assertEquals(distribution.toString(), "DagumDistribution[1, 2, 3]");
  }

  public void testFailNonPositive() {
    AssertFail.of(() -> DagumDistribution.of(0, 2, 3));
    AssertFail.of(() -> DagumDistribution.of(1, 0, 3));
    AssertFail.of(() -> DagumDistribution.of(1, 2, 0));
  }

  public void testFailNegative() {
    AssertFail.of(() -> DagumDistribution.of(-1, 2, 3));
    AssertFail.of(() -> DagumDistribution.of(1, -1, 3));
    AssertFail.of(() -> DagumDistribution.of(1, 2, -1));
  }
}
