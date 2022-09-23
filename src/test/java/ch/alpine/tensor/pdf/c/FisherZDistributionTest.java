// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.PDF;

class FisherZDistributionTest {
  @Test
  void testPdf1() {
    Distribution distribution = FisherZDistribution.of(2.1, 3.2);
    PDF pdf = PDF.of(distribution);
    Tolerance.CHOP.requireClose(pdf.at(RealScalar.of(0.3)), RealScalar.of(0.5125054381409268));
    assertTrue(distribution.toString().startsWith("FisherZDistribution["));
  }

  @Test
  void testFails() {
    assertThrows(Exception.class, () -> FisherZDistribution.of(1, 0));
    assertThrows(Exception.class, () -> FisherZDistribution.of(0, 1));
  }
}
