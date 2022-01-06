// code by jph
package ch.alpine.tensor.pdf;

import java.io.IOException;
import java.util.Random;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class MaxwellDistributionTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(MaxwellDistribution.of(1.3));
    Tolerance.CHOP.requireClose( //
        PDF.of(distribution).at(RealScalar.of(0.7)), //
        RealScalar.of(0.15393813960787467));
    assertEquals(PDF.of(distribution).at(RealScalar.of(0)), RealScalar.of(0));
    assertEquals(PDF.of(distribution).at(RealScalar.of(-1)), RealScalar.of(0));
    Tolerance.CHOP.requireClose( //
        CDF.of(distribution).p_lessEquals(RealScalar.of(0.7)), //
        RealScalar.of(0.03809089734621629));
    Tolerance.CHOP.requireClose(CDF.of(distribution).p_lessEquals(RealScalar.of(0)), RealScalar.of(0));
    assertEquals(CDF.of(distribution).p_lessEquals(RealScalar.of(-1)), RealScalar.of(0));
    Tolerance.CHOP.requireClose( //
        Mean.of(distribution), //
        RealScalar.of(2.0744998580874503));
    Tolerance.CHOP.requireClose( //
        Variance.of(distribution), //
        RealScalar.of(0.76645033879515));
    assertTrue(distribution.toString().startsWith("MaxwellDistribution["));
  }

  public void testMarkov() {
    Random random = new Random();
    Distribution distribution = MaxwellDistribution.of(0.1 + random.nextDouble());
    TestHelper.markov(distribution);
  }

  public void testSigmaFail() {
    AssertFail.of(() -> MaxwellDistribution.of(0));
    AssertFail.of(() -> MaxwellDistribution.of(-1));
    AssertFail.of(() -> MaxwellDistribution.of(Quantity.of(2, "m")));
  }
}
