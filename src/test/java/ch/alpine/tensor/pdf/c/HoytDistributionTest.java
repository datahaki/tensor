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
import ch.alpine.tensor.pdf.TestMarkovChebyshev;

class HoytDistributionTest {
  @Test
  void testPdf() {
    Distribution distribution = HoytDistribution.of(0.7, 2.3);
    PDF pdf = PDF.of(distribution);
    Tolerance.CHOP.requireClose(pdf.at(RealScalar.of(0.4)), RealScalar.of(0.34219868854909263));
    assertEquals(pdf.at(RealScalar.of(-1)), RealScalar.ZERO);
    assertTrue(distribution.toString().startsWith("HoytDistribution["));
  }

  @Test
  void testMonotonous() {
    TestMarkovChebyshev.monotonous(HoytDistribution.of(0.7, 2.3));
  }

  @Test
  void testFail() {
    assertThrows(Exception.class, () -> HoytDistribution.of(1.7, 2.3));
    assertThrows(Exception.class, () -> HoytDistribution.of(0.7, -2.3));
  }
}
