// code by jph
package ch.alpine.tensor.pdf;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.pdf.c.ExponentialDistribution;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;
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
