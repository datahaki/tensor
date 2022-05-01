// code by jph
package ch.alpine.tensor.pdf;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.pdf.c.ErlangDistribution;

class CDFTest {
  @Test
  public void testCDFFail() {
    Distribution distribution = ErlangDistribution.of(3, RealScalar.of(0.3));
    assertThrows(IllegalArgumentException.class, () -> CDF.of(distribution));
  }

  @Test
  public void testNullFail() {
    assertThrows(NullPointerException.class, () -> CDF.of(null));
  }
}
