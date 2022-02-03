// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.IOException;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.Expectation;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class GammaDistributionTest extends TestCase {
  public void testPdf() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(GammaDistribution.of(RealScalar.of(1.123), RealScalar.of(2.3)));
    PDF pdf = PDF.of(distribution);
    Chop._08.requireClose(pdf.at(RealScalar.of(0.78)), DoubleScalar.of(0.28770929331586703));
    assertTrue(Scalars.isZero(pdf.at(RealScalar.of(-0.3))));
  }

  public void testExp() {
    Distribution distribution = GammaDistribution.of(RealScalar.of(1.0), RealScalar.of(2.3));
    assertTrue(distribution instanceof ExponentialDistribution);
  }

  public void testMean() {
    Scalar a = RealScalar.of(1.123);
    Scalar b = RealScalar.of(2.3);
    Distribution distribution = GammaDistribution.of(a, b);
    assertEquals(Expectation.mean(distribution), a.multiply(b));
    assertEquals(Expectation.variance(distribution), a.multiply(b).multiply(b));
  }

  public void testToString() {
    Scalar a = RealScalar.of(1.123);
    Scalar b = RealScalar.of(2.3);
    Distribution distribution = GammaDistribution.of(a, b);
    assertEquals(distribution.toString(), "GammaDistribution[1.123, 2.3]");
  }

  public void testFail() {
    AssertFail.of(() -> GammaDistribution.of(RealScalar.of(-1.0), RealScalar.of(2.3)));
    AssertFail.of(() -> GammaDistribution.of(RealScalar.of(0.1), RealScalar.of(-2.3)));
  }
}
