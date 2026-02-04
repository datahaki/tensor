// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.pdf.UnivariateDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Clips;

class RayleighDistributionTest {
  @Test
  void testSimple() throws ClassNotFoundException, IOException {
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
    UnivariateDistribution ud = (UnivariateDistribution) distribution;
    assertEquals(ud.support(), Clips.positive(Double.POSITIVE_INFINITY));
  }

  @Test
  void testRandom() {
    RayleighDistribution distribution = (RayleighDistribution) RayleighDistribution.of(1.3);
    RandomVariate.of(distribution, 100);
    Scalar q0 = distribution.protected_quantile(RealScalar.ZERO);
    Scalar q1 = distribution.protected_quantile(RealScalar.of(Math.nextDown(1.0)));
    assertTrue(FiniteScalarQ.of(q0));
    assertTrue(FiniteScalarQ.of(q1));
  }

  @Test
  void testMonotonous() {
    TestMarkovChebyshev.monotonous(RayleighDistribution.of(1.3));
  }

  @Test
  void testSigmaFail() {
    assertThrows(Throw.class, () -> RayleighDistribution.of(0));
    assertThrows(Throw.class, () -> RayleighDistribution.of(-1));
    assertThrows(Throw.class, () -> RayleighDistribution.of(Quantity.of(2, "m")));
  }
}
