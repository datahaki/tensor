// code by jph
package ch.alpine.tensor.pdf.c;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;

class FRatioDistributionTest {
  @Test
  void testMean() {
    Distribution distribution = FRatioDistribution.of(2.1, 3.2);
    Tolerance.CHOP.requireClose(Mean.of(distribution), RealScalar.of(2.6666666666666665));
  }

  @Test
  void testVariance() {
    Distribution distribution = FRatioDistribution.of(5.1, 6.2);
    Tolerance.CHOP.requireClose(Variance.of(distribution), RealScalar.of(3.6124752931475625));
  }
}
