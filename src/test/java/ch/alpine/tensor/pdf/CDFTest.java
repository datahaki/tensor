// code by jph
package ch.alpine.tensor.pdf;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.pdf.c.ErlangDistribution;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class CDFTest extends TestCase {
  public void testCDFFail() {
    Distribution distribution = ErlangDistribution.of(3, RealScalar.of(0.3));
    AssertFail.of(() -> CDF.of(distribution));
  }

  public void testNullFail() {
    AssertFail.of(() -> CDF.of(null));
  }
}
