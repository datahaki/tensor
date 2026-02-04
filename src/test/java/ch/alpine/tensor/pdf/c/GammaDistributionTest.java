// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.Expectation;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.sca.Chop;

class GammaDistributionTest {
  @Test
  void testPdf() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(GammaDistribution.of(RealScalar.of(1.123), RealScalar.of(2.3)));
    PDF pdf = PDF.of(distribution);
    Chop._08.requireClose(pdf.at(RealScalar.of(0.78)), DoubleScalar.of(0.28770929331586703));
    assertTrue(Scalars.isZero(pdf.at(RealScalar.of(-0.3))));
  }

  @Test
  void testExpErlang() {
    assertInstanceOf(ExponentialDistribution.class, GammaDistribution.of(RealScalar.of(1), RealScalar.of(2.3)));
    assertInstanceOf(ErlangDistribution.class, GammaDistribution.of(RealScalar.of(3), RealScalar.of(2.3)));
  }

  @Test
  void testMean() {
    Scalar a = RealScalar.of(1.123);
    Scalar b = RealScalar.of(2.3);
    Distribution distribution = GammaDistribution.of(a, b);
    assertEquals(Expectation.mean(distribution), a.multiply(b));
    assertEquals(Expectation.variance(distribution), a.multiply(b).multiply(b));
  }

  @Test
  void testMonotonous() {
    TestMarkovChebyshev.monotonous(GammaDistribution.of(1.5, 1.3));
  }

  @ParameterizedTest
  @ValueSource(strings = { "0.7", "1.1", "1.3" })
  void testRandom(String string) {
    Scalar alpha = Scalars.fromString(string);
    Distribution d1 = GammaDistribution.of(alpha, RealScalar.of(2.3));
    Tensor r1 = RandomVariate.of(d1, new Random(3), 10);
    Tensor r2 = RandomVariate.of(d1, new Random(3), 10);
    Tolerance.CHOP.requireClose(r1, r2);
  }

  @Test
  void testToString() {
    Scalar a = RealScalar.of(1.123);
    Scalar b = RealScalar.of(2.3);
    Distribution distribution = GammaDistribution.of(a, b);
    assertEquals(distribution.toString(), "GammaDistribution[1.123, 2.3]");
  }

  @Test
  void testFail() {
    assertThrows(Throw.class, () -> GammaDistribution.of(RealScalar.of(-1.0), RealScalar.of(2.3)));
    assertThrows(Throw.class, () -> GammaDistribution.of(RealScalar.of(0.1), RealScalar.of(-2.3)));
  }
}
