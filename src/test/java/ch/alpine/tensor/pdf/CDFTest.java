// code by jph
package ch.alpine.tensor.pdf;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.pdf.c.StudentTDistribution;

class CDFTest {
  @Test
  void testCDFFail() {
    Distribution distribution = StudentTDistribution.of(1, 1, 1);
    assertThrows(IllegalArgumentException.class, () -> CDF.of(distribution));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> CDF.of(null));
  }
}
