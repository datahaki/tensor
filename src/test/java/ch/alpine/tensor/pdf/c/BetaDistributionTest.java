// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;

class BetaDistributionTest {
  @Test
  void testPdf() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(BetaDistribution.of(2, 3));
    PDF pdf = PDF.of(distribution);
    Tolerance.CHOP.requireClose(pdf.at(RealScalar.of(0.4)), RealScalar.of(1.728));
    Tolerance.CHOP.requireZero(pdf.at(RealScalar.of(-0.1)));
    Tolerance.CHOP.requireZero(pdf.at(RealScalar.of(+1.1)));
    Tensor r1 = RandomVariate.of(distribution, new Random(3), 100);
    Tensor r2 = RandomVariate.of(distribution, new Random(3), 100);
    assertEquals(r1, r2);
  }

  @Test
  void testMeanVar() {
    Distribution distribution = BetaDistribution.of(5, 7.3);
    Tolerance.CHOP.requireClose(Mean.of(distribution), RealScalar.of(0.4065040650406504));
    Tolerance.CHOP.requireClose(Variance.of(distribution), RealScalar.of(0.018139737604968197));
    assertEquals(distribution.toString(), "BetaDistribution[5, 7.3]");
  }

  @Test
  void testMonotonous() {
    TestMarkovChebyshev.monotonous(BetaDistribution.of(3, 2));
  }

  @Test
  void testFailNonPositive() {
    assertThrows(Throw.class, () -> BetaDistribution.of(0, 3));
    assertThrows(Throw.class, () -> BetaDistribution.of(2, 0));
  }
}
