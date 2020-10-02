// code by jph
package ch.ethz.idsc.tensor.pdf;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class PDFTest extends TestCase {
  public void testExponentialDistribution() {
    PDF pdf = PDF.of(ExponentialDistribution.of(RationalScalar.of(3, 2)));
    Scalar density = pdf.at(RealScalar.of(3));
    Chop._15.requireClose(density, RealScalar.of(0.016663494807363458));
  }

  public void testNullFail() {
    AssertFail.of(() -> PDF.of(null));
  }
}
