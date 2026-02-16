// code by jph
package ch.alpine.tensor.pdf;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.pdf.c.ExponentialDistribution;
import ch.alpine.tensor.sca.Chop;

class PDFTest {
  @Test
  void testExponentialDistribution() {
    PDF pdf = PDF.of(ExponentialDistribution.of(Rational.of(3, 2)));
    Scalar density = pdf.at(RealScalar.of(3));
    Chop._15.requireClose(density, RealScalar.of(0.016663494807363458));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> PDF.of(null));
  }
}
