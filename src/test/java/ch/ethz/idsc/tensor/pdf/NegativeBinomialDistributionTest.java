// code by jph
package ch.ethz.idsc.tensor.pdf;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Variance;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class NegativeBinomialDistributionTest extends TestCase {
  public void testSimple() {
    Distribution distribution = NegativeBinomialDistribution.of(4, RationalScalar.of(1, 3));
    assertEquals(PDF.of(distribution).at(RealScalar.of(3)), RationalScalar.of(160, 2187));
    assertEquals(CDF.of(distribution).p_lessEquals(RealScalar.of(3)), RationalScalar.of(379, 2187));
    RandomVariate.of(distribution, 1000);
    assertEquals(Mean.of(distribution), RealScalar.of(8));
    assertEquals(Variance.of(distribution), RealScalar.of(8 * 3));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    assertEquals(inverseCDF.quantile(RationalScalar.HALF), RealScalar.of(7));
    Scalar quantile = inverseCDF.quantile(RealScalar.of(1 - 1e-8));
    // confirmed with mathematica
    // InverseCDF[NegativeBinomialDistribution[4, 1/3], 1 - 1*^-8]
    assertEquals(quantile, RealScalar.of(64));
  }

  public void testFails() {
    AssertFail.of(() -> NegativeBinomialDistribution.of(-1, RationalScalar.HALF));
    AssertFail.of(() -> NegativeBinomialDistribution.of(2, RealScalar.ZERO));
    AssertFail.of(() -> NegativeBinomialDistribution.of(2, RealScalar.of(1.1)));
  }
}
