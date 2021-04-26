// code by jph
package ch.ethz.idsc.tensor.pdf;

import java.io.IOException;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Variance;
import ch.ethz.idsc.tensor.usr.AssertFail;
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
