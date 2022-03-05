// code by jph
package ch.alpine.tensor.pdf.c;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.red.CentralMoment;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class StandardNormalDistributionTest extends TestCase {
  public void testPdfZero() {
    Scalar x = StandardNormalDistribution.INSTANCE.at(RealScalar.ZERO);
    assertTrue(x.toString().startsWith("0.398942280"));
  }

  public void testPdfOneSymmetric() {
    Scalar x = StandardNormalDistribution.INSTANCE.at(RealScalar.ONE);
    Scalar xn = StandardNormalDistribution.INSTANCE.at(RealScalar.ONE.negate());
    assertTrue(x.toString().startsWith("0.241970724"));
    assertEquals(x, xn);
  }

  public void testCdf() {
    CDF cdf = StandardNormalDistribution.INSTANCE;
    Tolerance.CHOP.requireClose(cdf.p_lessEquals(RealScalar.ZERO), RealScalar.of(0.5));
    Scalar p = cdf.p_lessThan(RealScalar.of(0.3));
    assertTrue(p.toString().startsWith("0.617911"));
    Scalar q = cdf.p_lessThan(RealScalar.of(-0.3));
    assertEquals(p.add(q), RealScalar.ONE);
  }

  public void testQuantile() {
    Tolerance.CHOP.requireClose( //
        StandardNormalDistribution.INSTANCE.quantile(RealScalar.of(0.6307)), //
        RealScalar.of(0.3337078836526057));
  }

  public void testInverseCDF() {
    Distribution distribution = StandardNormalDistribution.INSTANCE;
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    AssertFail.of(() -> inverseCDF.quantile(RealScalar.of(1.1)));
    AssertFail.of(() -> inverseCDF.quantile(RealScalar.of(-0.1)));
  }

  public void testMeanVar() {
    Distribution distribution = NormalDistribution.standard();
    assertEquals(Mean.of(distribution), RealScalar.ZERO);
    assertEquals(Variance.of(distribution), RealScalar.ONE);
    assertEquals(distribution.toString(), "StandardNormalDistribution");
    assertEquals(CentralMoment.of(distribution, 0), RealScalar.ONE);
    assertEquals(CentralMoment.of(distribution, 1), RealScalar.ZERO);
    assertEquals(CentralMoment.of(distribution, 2), RealScalar.ONE);
    assertEquals(CentralMoment.of(distribution, 3), RealScalar.ZERO);
    assertEquals(CentralMoment.of(distribution, 4), RealScalar.of(3));
  }
}
