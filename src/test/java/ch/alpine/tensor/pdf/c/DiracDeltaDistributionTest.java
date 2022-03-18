// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.red.CentralMoment;

public class DiracDeltaDistributionTest {
  @Test
  public void testCdf() {
    Distribution distribution = DiracDeltaDistribution.of(RealScalar.TWO);
    CDF cdf = CDF.of(distribution);
    assertEquals(cdf.p_lessThan(RealScalar.of(1)), RealScalar.ZERO);
    assertEquals(cdf.p_lessThan(RealScalar.of(2)), RealScalar.ZERO);
    assertEquals(cdf.p_lessThan(RealScalar.of(3)), RealScalar.ONE);
    assertEquals(cdf.p_lessEquals(RealScalar.of(1)), RealScalar.ZERO);
    assertEquals(cdf.p_lessEquals(RealScalar.of(2)), RealScalar.ONE);
    assertEquals(cdf.p_lessEquals(RealScalar.of(3)), RealScalar.ONE);
  }

  @Test
  public void testRandom() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(DiracDeltaDistribution.of(Pi.VALUE));
    assertEquals(RandomVariate.of(distribution), Pi.VALUE);
    assertEquals(CentralMoment.of(distribution, 0), RealScalar.ONE);
    assertEquals(CentralMoment.of(distribution, 1), RealScalar.ZERO);
    assertEquals(CentralMoment.of(distribution, 2), RealScalar.ZERO);
  }
}
