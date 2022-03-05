// code by jph
package ch.alpine.tensor.pdf.d;

import java.io.IOException;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.num.Binomial;
import ch.alpine.tensor.num.Boole;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class NegativeBinomialDistributionTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(NegativeBinomialDistribution.of(4, RationalScalar.of(1, 3)));
    assertEquals(PDF.of(distribution).at(RealScalar.of(3)), RationalScalar.of(160, 2187));
    assertEquals(CDF.of(distribution).p_lessEquals(RealScalar.of(3)), RationalScalar.of(379, 2187));
    RandomVariate.of(distribution, 100);
    assertEquals(Mean.of(distribution), RealScalar.of(8));
    assertEquals(Variance.of(distribution), RealScalar.of(8 * 3));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    assertEquals(inverseCDF.quantile(RationalScalar.HALF), RealScalar.of(7));
    // confirmed with Mathematica, for instance
    // InverseCDF[NegativeBinomialDistribution[4, 1/3], 1 - 1*^-8]
    assertEquals(inverseCDF.quantile(RealScalar.of(1 - 1e-7)), RealScalar.of(57));
    assertEquals(inverseCDF.quantile(RealScalar.of(1 - 1e-8)), RealScalar.of(64));
    assertEquals(inverseCDF.quantile(RealScalar.of(1 - 1e-9)), RealScalar.of(70));
    assertEquals(inverseCDF.quantile(RealScalar.of(1 - 1e-10)), RealScalar.of(76));
  }

  public void testPOne() {
    int k = 0;
    for (int n = 0; n < 4; ++n) {
      assertEquals(Power.of(RealScalar.ZERO, n), Boole.of(n == 0));
      assertEquals(Binomial.of(n - 1 + k, n - 1), RealScalar.ONE);
      Distribution distribution = NegativeBinomialDistribution.of(0, 1);
      assertEquals(PDF.of(distribution).at(RealScalar.ZERO), RealScalar.ONE);
      assertEquals(PDF.of(distribution).at(RealScalar.of(1)), RealScalar.ZERO);
      assertTrue(distribution.toString().startsWith("NegativeBinomialDistribution["));
    }
  }

  public void testFails() {
    AssertFail.of(() -> NegativeBinomialDistribution.of(-1, RationalScalar.HALF));
    AssertFail.of(() -> NegativeBinomialDistribution.of(2, RealScalar.ZERO));
    AssertFail.of(() -> NegativeBinomialDistribution.of(2, RealScalar.of(1.1)));
  }
}
