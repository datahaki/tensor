// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;

class RiceDistributionTest {
  @Test
  void testPdf1() {
    Distribution distribution = RiceDistribution.of(1.2, 2.23);
    PDF pdf = PDF.of(distribution);
    Tolerance.CHOP.requireClose(pdf.at(Pi.VALUE), RealScalar.of(0.23280009427791543));
    Tolerance.CHOP.requireClose(Mean.of(distribution), RealScalar.of(2.993643903885086));
    Tolerance.CHOP.requireClose(Variance.of(distribution), RealScalar.of(2.4238961767316614));
    assertEquals(pdf.at(RealScalar.ZERO), RealScalar.ZERO);
    assertTrue(distribution.toString().startsWith("RiceDistribution["));
  }

  @Test
  void testMonotonous() {
    TestMarkovChebyshev.monotonous(RiceDistribution.of(1.2, 2.23));
  }

  @Test
  void testFails() {
    assertThrows(Exception.class, () -> RiceDistribution.of(+1.2, 0));
    assertThrows(Exception.class, () -> RiceDistribution.of(-1.2, 1));
  }
}
