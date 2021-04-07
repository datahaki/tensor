// code by jph
package ch.ethz.idsc.tensor.pdf;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Variance;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ChiSquareDistributionTest extends TestCase {
  public void testSimple() {
    Distribution distribution = ChiSquareDistribution.of(2.3);
    Scalar scalar = PDF.of(distribution).at(RealScalar.of(1.4));
    Chop._12.requireClose(scalar, RealScalar.of(0.2522480844885552));
    assertEquals(Mean.of(distribution), RealScalar.of(2.3));
    assertEquals(Variance.of(distribution), RealScalar.of(2.3 + 2.3));
  }

  public void testFails() {
    AssertFail.of(() -> ChiSquareDistribution.of(0));
    AssertFail.of(() -> ChiSquareDistribution.of(-2.3));
    AssertFail.of(() -> ChiSquareDistribution.of(Quantity.of(2, "m")));
  }
}
