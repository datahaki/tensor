// code by jph
package ch.ethz.idsc.tensor.pdf;

import java.io.IOException;

import ch.ethz.idsc.tensor.NumberQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Variance;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class RayleighDistributionTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(RayleighDistribution.of(1.3));
    Tolerance.CHOP.requireClose( //
        PDF.of(distribution).at(RealScalar.of(0.7)), //
        RealScalar.of(0.3583038580505363));
    assertEquals(PDF.of(distribution).at(RealScalar.of(0)), RealScalar.of(0));
    assertEquals(PDF.of(distribution).at(RealScalar.of(-1)), RealScalar.of(0));
    Tolerance.CHOP.requireClose( //
        CDF.of(distribution).p_lessEquals(RealScalar.of(0.7)), //
        RealScalar.of(0.13495211413513364));
    assertEquals(CDF.of(distribution).p_lessEquals(RealScalar.of(0)), RealScalar.of(0));
    assertEquals(CDF.of(distribution).p_lessEquals(RealScalar.of(-1)), RealScalar.of(0));
    Tolerance.CHOP.requireClose( //
        Mean.of(distribution), //
        RealScalar.of(1.6293083785101503));
    Tolerance.CHOP.requireClose( //
        Variance.of(distribution), //
        RealScalar.of(0.7253542077166248));
    Tolerance.CHOP.requireClose( //
        InverseCDF.of(distribution).quantile(RealScalar.of(0.7)), //
        RealScalar.of(2.017282349752177));
    assertTrue(distribution.toString().startsWith("RayleighDistribution["));
  }

  public void testRandom() {
    RayleighDistribution distribution = (RayleighDistribution) RayleighDistribution.of(RealScalar.of(1.3));
    RandomVariate.of(distribution, 100);
    Scalar q0 = distribution.protected_quantile(RealScalar.ZERO);
    Scalar q1 = distribution.protected_quantile(RealScalar.of(Math.nextDown(1.0)));
    NumberQ.require(q0);
    NumberQ.require(q1);
  }

  public void testSigmaFail() {
    AssertFail.of(() -> RayleighDistribution.of(0));
    AssertFail.of(() -> RayleighDistribution.of(-1));
    AssertFail.of(() -> RayleighDistribution.of(Quantity.of(2, "m")));
  }
}
