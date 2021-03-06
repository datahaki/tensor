// code by jph
package ch.alpine.tensor.pdf;

import java.io.IOException;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class BetaDistributionTest extends TestCase {
  public void testPdf() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(BetaDistribution.of(2, 3));
    PDF pdf = PDF.of(distribution);
    Tolerance.CHOP.requireClose(pdf.at(RealScalar.of(0.4)), RealScalar.of(1.728));
    Tolerance.CHOP.requireZero(pdf.at(RealScalar.of(-0.1)));
    Tolerance.CHOP.requireZero(pdf.at(RealScalar.of(+1.1)));
  }

  public void testMeanVar() {
    Distribution distribution = BetaDistribution.of(5, 7.3);
    Tolerance.CHOP.requireClose(Mean.of(distribution), RealScalar.of(0.4065040650406504));
    Tolerance.CHOP.requireClose(Variance.of(distribution), RealScalar.of(0.018139737604968197));
  }

  public void testFailNonPositive() {
    AssertFail.of(() -> BetaDistribution.of(0, 3));
    AssertFail.of(() -> BetaDistribution.of(2, 0));
  }
}
