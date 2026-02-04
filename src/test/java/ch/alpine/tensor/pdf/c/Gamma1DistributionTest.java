// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.io.IOException;
import java.util.Random;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.StandardDeviation;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Chop;

class Gamma1DistributionTest {
  @ParameterizedTest
  @ValueSource(strings = { "0.3", "1", "1.3" })
  void testPDF(String string) throws ClassNotFoundException, IOException {
    Scalar alpha = Scalars.fromString(string);
    Distribution d1 = new GammaDistribution(alpha, RealScalar.ONE);
    Distribution d2 = new Gamma1Distribution(alpha);
    Tensor samples = Subdivide.of(1e-10, 10.0, 123);
    Chop._08.requireClose(samples.map(PDF.of(d1)::at), samples.map(PDF.of(d2)::at));
    Serialization.copy(d1);
    Serialization.copy(d2);
    assertEquals(Mean.of(d1), Mean.of(d2));
    assertEquals(Variance.of(d1), Variance.of(d2));
    assertEquals(StandardDeviation.of(d1), StandardDeviation.of(d2));
    assertEquals(d1.toString(), d2.toString());
    assertEquals(PDF.of(d2).at(RealScalar.of(-2)), RealScalar.ZERO);
  }

  @ParameterizedTest
  @ValueSource(strings = { "0.7", "1.1", "1.3" })
  void testRandom(String string) {
    Scalar alpha = Scalars.fromString(string);
    Distribution d1 = GammaDistribution.of(alpha, RealScalar.ONE);
    Distribution d2 = new Gamma1Distribution(alpha);
    assertInstanceOf(Gamma1Distribution.class, d1);
    assertInstanceOf(Gamma1Distribution.class, d2);
    Tensor r1 = RandomVariate.of(d1, new Random(3), 10);
    Tensor r2 = RandomVariate.of(d2, new Random(3), 10);
    Tolerance.CHOP.requireClose(r1, r2);
  }
}
