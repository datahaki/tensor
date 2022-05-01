// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.Expectation;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.sca.Chop;

public class GammaDistributionTest {
  @Test
  public void testPdf() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(GammaDistribution.of(RealScalar.of(1.123), RealScalar.of(2.3)));
    PDF pdf = PDF.of(distribution);
    Chop._08.requireClose(pdf.at(RealScalar.of(0.78)), DoubleScalar.of(0.28770929331586703));
    assertTrue(Scalars.isZero(pdf.at(RealScalar.of(-0.3))));
  }

  @Test
  public void testExp() {
    Distribution distribution = GammaDistribution.of(RealScalar.of(1.0), RealScalar.of(2.3));
    assertInstanceOf(ExponentialDistribution.class, distribution);
  }

  @Test
  public void testMean() {
    Scalar a = RealScalar.of(1.123);
    Scalar b = RealScalar.of(2.3);
    Distribution distribution = GammaDistribution.of(a, b);
    assertEquals(Expectation.mean(distribution), a.multiply(b));
    assertEquals(Expectation.variance(distribution), a.multiply(b).multiply(b));
  }

  @Test
  public void testToString() {
    Scalar a = RealScalar.of(1.123);
    Scalar b = RealScalar.of(2.3);
    Distribution distribution = GammaDistribution.of(a, b);
    assertEquals(distribution.toString(), "GammaDistribution[1.123, 2.3]");
  }

  @Test
  public void testFail() {
    assertThrows(TensorRuntimeException.class, () -> GammaDistribution.of(RealScalar.of(-1.0), RealScalar.of(2.3)));
    assertThrows(TensorRuntimeException.class, () -> GammaDistribution.of(RealScalar.of(0.1), RealScalar.of(-2.3)));
  }
}
