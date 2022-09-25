// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.jet.DateObject;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.red.CentralMoment;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Quantile;
import ch.alpine.tensor.red.Variance;

class DiracDeltaDistributionTest {
  @Test
  void testProb() {
    Distribution distribution = DiracDeltaDistribution.of(Pi.VALUE);
    PDF pdf = PDF.of(distribution);
    assertEquals(pdf.at(RealScalar.of(3)), RealScalar.of(0));
    assertFalse(FiniteScalarQ.of(pdf.at(Pi.VALUE)));
  }

  @Test
  void testCdf() {
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
  void testRandom() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(DiracDeltaDistribution.of(Pi.VALUE));
    assertEquals(RandomVariate.of(distribution), Pi.VALUE);
    assertEquals(CentralMoment.of(distribution, 0), RealScalar.ONE);
    assertEquals(CentralMoment.of(distribution, 1), RealScalar.ZERO);
    assertEquals(CentralMoment.of(distribution, 2), RealScalar.ZERO);
  }

  @Test
  void testQuantile() {
    Distribution distribution = DiracDeltaDistribution.of(RealScalar.TWO);
    assertEquals(Quantile.of(distribution).apply(RealScalar.ONE), RealScalar.TWO);
  }

  @Test
  void testDateTime() {
    Scalar d1 = DateObject.of(LocalDateTime.of(2022, 11, 13, 10, 12));
    Scalar d2 = DateObject.of(LocalDateTime.of(2022, 11, 13, 10, 13));
    Distribution distribution = DiracDeltaDistribution.of(d1);
    PDF pdf = PDF.of(distribution);
    assertTrue(Scalars.isZero(pdf.at(d2)));
    CDF cdf = CDF.of(distribution);
    assertEquals(cdf.p_lessThan(d2), RealScalar.ONE);
    assertEquals(distribution.toString(), "DiracDeltaDistribution[2022-11-13T10:12]");
  }

  @Test
  void testMeanVar() {
    Distribution distribution = DiracDeltaDistribution.of(RealScalar.of(100));
    assertEquals(Mean.of(distribution), RealScalar.of(100));
    assertEquals(Variance.of(distribution), RealScalar.of(0));
  }

  @Test
  void testMonotonous() {
    TestMarkovChebyshev.monotonous(DiracDeltaDistribution.of(RealScalar.of(1)));
  }
}
