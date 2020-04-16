// code by jph
package ch.ethz.idsc.tensor.pdf;

import java.io.IOException;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Variance;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class BetaDistributionTest extends TestCase {
  public void testPdf() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(BetaDistribution.of(2, 3));
    PDF pdf = PDF.of(distribution);
    Chop._12.requireClose(pdf.at(RealScalar.of(0.4)), RealScalar.of(1.728));
    Chop._12.requireZero(pdf.at(RealScalar.of(-0.1)));
    Chop._12.requireZero(pdf.at(RealScalar.of(+1.1)));
  }

  public void testMeanVar() {
    Distribution distribution = BetaDistribution.of(5, 7.3);
    Chop._10.requireClose(Mean.of(distribution), RealScalar.of(0.4065040650406504));
    Chop._10.requireClose(Variance.of(distribution), RealScalar.of(0.018139737604968197));
  }

  public void testFailNonPositive() {
    try {
      BetaDistribution.of(0, 3);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      BetaDistribution.of(2, 0);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
