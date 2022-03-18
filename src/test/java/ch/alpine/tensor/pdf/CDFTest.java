// code by jph
package ch.alpine.tensor.pdf;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.pdf.c.ErlangDistribution;
import ch.alpine.tensor.usr.AssertFail;

public class CDFTest {
  @Test
  public void testCDFFail() {
    Distribution distribution = ErlangDistribution.of(3, RealScalar.of(0.3));
    AssertFail.of(() -> CDF.of(distribution));
  }

  @Test
  public void testNullFail() {
    AssertFail.of(() -> CDF.of(null));
  }
}
