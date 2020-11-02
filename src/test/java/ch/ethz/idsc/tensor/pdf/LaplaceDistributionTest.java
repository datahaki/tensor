// code by jph
package ch.ethz.idsc.tensor.pdf;

import java.io.IOException;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Variance;
import junit.framework.TestCase;

public class LaplaceDistributionTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(LaplaceDistribution.of(2, 5));
    PDF pdf = PDF.of(distribution);
    Tolerance.CHOP.requireClose(pdf.at(RealScalar.of(-3)), RealScalar.of(0.036787944117144235));
    Tolerance.CHOP.requireClose(pdf.at(RealScalar.of(+3)), RealScalar.of(0.0818730753077982));
    CDF cdf = CDF.of(distribution);
    Tolerance.CHOP.requireClose(cdf.p_lessEquals(RealScalar.of(-3)), RealScalar.of(0.18393972058572117));
    Tolerance.CHOP.requireClose(cdf.p_lessEquals(RealScalar.of(+3)), RealScalar.of(0.5906346234610091));
    InverseCDF inverseCdf = InverseCDF.of(distribution);
    Tolerance.CHOP.requireClose(inverseCdf.quantile(RealScalar.of(0.1)), RealScalar.of(-6.047189562170502));
    Tolerance.CHOP.requireClose(inverseCdf.quantile(RealScalar.of(0.9)), RealScalar.of(10.047189562170502));
    assertEquals(distribution.toString(), "LaplaceDistribution[2, 5]");
  }

  public void testRandomMeanVar() {
    Distribution distribution = LaplaceDistribution.of(3, 2);
    RandomVariate.of(distribution, 100);
    assertEquals(ExactScalarQ.require(Mean.of(distribution)), RealScalar.of(3));
    assertEquals(ExactScalarQ.require(Variance.of(distribution)), RealScalar.of(8));
  }
}
