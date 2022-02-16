// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.IOException;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.RandomVariate;
import junit.framework.TestCase;

public class DiracDistributionTest extends TestCase {
  public void testCdf() {
    DiracDistribution diracDistribution = new DiracDistribution(RealScalar.TWO);
    assertEquals(diracDistribution.p_lessThan(RealScalar.of(1)), RealScalar.ZERO);
    assertEquals(diracDistribution.p_lessThan(RealScalar.of(2)), RealScalar.ZERO);
    assertEquals(diracDistribution.p_lessThan(RealScalar.of(3)), RealScalar.ONE);
    assertEquals(diracDistribution.p_lessEquals(RealScalar.of(1)), RealScalar.ZERO);
    assertEquals(diracDistribution.p_lessEquals(RealScalar.of(2)), RealScalar.ONE);
    assertEquals(diracDistribution.p_lessEquals(RealScalar.of(3)), RealScalar.ONE);
  }

  public void testRandom() throws ClassNotFoundException, IOException {
    DiracDistribution diracDistribution = Serialization.copy(new DiracDistribution(Pi.VALUE));
    assertEquals(RandomVariate.of(diracDistribution), Pi.VALUE);
  }
}