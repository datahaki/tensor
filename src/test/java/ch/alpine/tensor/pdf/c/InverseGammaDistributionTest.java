// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;

class InverseGammaDistributionTest {
  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(InverseGammaDistribution.of(1.2, 2.3));
    PDF pdf = PDF.of(distribution);
    Scalar f = pdf.at(RealScalar.of(4));
    Tolerance.CHOP.requireClose(f, RealScalar.of(0.07886777547414911));
    CDF cdf = CDF.of(distribution);
    assertThrows(Exception.class, () -> cdf.p_lessThan(RealScalar.of(10)));
    assertTrue(distribution.toString().startsWith("InverseGammaDistribution["));
    Tolerance.CHOP.requireClose(Mean.of(distribution), RealScalar.of(11.5));
    assertEquals(Variance.of(distribution).toString(), "NaN");
  }

  @Test
  void testMean() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(InverseGammaDistribution.of(0.2, 2.3));
    assertEquals(Mean.of(distribution).toString(), "NaN");
  }

  @Test
  void testVariance() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(InverseGammaDistribution.of(2.2, 2.3));
    Tolerance.CHOP.requireClose(Variance.of(distribution), RealScalar.of(18.368055555555532));
    assertThrows(Exception.class, () -> RandomVariate.of(distribution));
  }

  @Test
  void testLevy() {
    Distribution d1 = InverseGammaDistribution.of(0.50000000000001, 0.7);
    Distribution d2 = InverseGammaDistribution.of(0.50000000000000, 0.7);
    assertInstanceOf(InverseGammaDistribution.class, d1);
    assertInstanceOf(LevyDistribution.class, d2);
    Tensor domain = Subdivide.of(0, 10, 100);
    Tensor res1 = domain.map(PDF.of(d1)::at);
    Tensor res2 = domain.map(PDF.of(d2)::at);
    Tolerance.CHOP.requireClose(res1, res2);
  }
}
