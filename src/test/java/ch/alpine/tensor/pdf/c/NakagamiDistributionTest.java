// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;

class NakagamiDistributionTest {
  @Test
  void testPdf1() {
    Distribution distribution = NakagamiDistribution.of(1, 2);
    PDF pdf = PDF.of(distribution);
    Tolerance.CHOP.requireClose(pdf.at(RealScalar.of(0.2)), RealScalar.of(0.19603973466135108));
    assertTrue(distribution.toString().startsWith("NakagamiDistribution["));
  }

  @Test
  void testPdf2() {
    Distribution distribution = NakagamiDistribution.of(0.3, 2.5);
    PDF pdf = PDF.of(distribution);
    Tolerance.CHOP.requireClose(pdf.at(RealScalar.of(3.2)), RealScalar.of(0.06503756850567931));
    Tolerance.CHOP.requireClose(Mean.of(distribution), RealScalar.of(1.1234378036814632));
    Tolerance.CHOP.requireClose(Variance.of(distribution), RealScalar.of(1.2378875012593704));
    assertEquals(pdf.at(RealScalar.ZERO), RealScalar.ZERO);
  }

  @Test
  void testFails() {
    assertThrows(Exception.class, () -> NakagamiDistribution.of(1, 0));
    assertThrows(Exception.class, () -> NakagamiDistribution.of(0, 1));
  }
}
