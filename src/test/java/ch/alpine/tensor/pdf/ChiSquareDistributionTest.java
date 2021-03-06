// code by jph
package ch.alpine.tensor.pdf;

import java.io.IOException;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ChiSquareDistributionTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(ChiSquareDistribution.of(2.3));
    PDF pdf = PDF.of(distribution);
    Scalar scalar = pdf.at(RealScalar.of(1.4));
    Chop._12.requireClose(scalar, RealScalar.of(0.2522480844885552));
    assertEquals(Mean.of(distribution), RealScalar.of(2.3));
    assertEquals(Variance.of(distribution), RealScalar.of(2.3 + 2.3));
    assertEquals(pdf.at(RealScalar.of(-1.4)), RealScalar.ZERO);
    AssertFail.of(() -> pdf.at(Quantity.of(2, "m")));
  }

  public void testFails() {
    AssertFail.of(() -> ChiSquareDistribution.of(0));
    AssertFail.of(() -> ChiSquareDistribution.of(-2.3));
    AssertFail.of(() -> ChiSquareDistribution.of(Quantity.of(2, "m")));
  }

  public void testToString() {
    Distribution distribution = ChiSquareDistribution.of(RationalScalar.of(3, 2));
    assertEquals(distribution.toString(), "ChiSquareDistribution[3/2]");
  }
}
