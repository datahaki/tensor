// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;

class FRatioDistributionTest {
  @Test
  void testMean() {
    Distribution distribution = FRatioDistribution.of(2.1, 3.2);
    PDF pdf = PDF.of(distribution);
    Tolerance.CHOP.requireClose(pdf.at(RealScalar.of(0.3)), RealScalar.of(0.6416415509425255));
    Tolerance.CHOP.requireClose(Mean.of(distribution), RealScalar.of(2.6666666666666665));
    assertEquals(pdf.at(RealScalar.ZERO), RealScalar.ZERO);
  }

  @Test
  void testVariance() {
    Distribution distribution = FRatioDistribution.of(5.1, 6.2);
    Tolerance.CHOP.requireClose(Variance.of(distribution), RealScalar.of(3.6124752931475625));
    assertTrue(distribution.toString().startsWith("FRatioDistribution["));
  }

  @Test
  void testMeanVarIndet() {
    Distribution distribution = FRatioDistribution.of(6.1, 1.2);
    assertFalse(FiniteScalarQ.of(Mean.of(distribution)));
    assertFalse(FiniteScalarQ.of(Variance.of(distribution)));
  }

  @Test
  void testMonotonous() {
    TestMarkovChebyshev.monotonous(FRatioDistribution.of(2.1, 3.2));
  }

  @Test
  void testFails() {
    assertThrows(Exception.class, () -> FRatioDistribution.of(1, 0));
    assertThrows(Exception.class, () -> FRatioDistribution.of(0, 1));
  }
}
