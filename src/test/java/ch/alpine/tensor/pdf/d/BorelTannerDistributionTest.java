// code by jph
package ch.alpine.tensor.pdf.d;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;

class BorelTannerDistributionTest {
  @Test
  void test() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(BorelTannerDistribution.of(0.3, 5));
    assertTrue(distribution.toString().startsWith("BorelTannerDistribution["));
    PDF pdf = PDF.of(distribution);
    Tolerance.CHOP.requireClose(pdf.at(RealScalar.of(10)), RealScalar.of(0.05040940672246232));
    Tolerance.CHOP.requireClose(Mean.of(distribution), RealScalar.of(7.142857142857143));
    Tolerance.CHOP.requireClose(Variance.of(distribution), RealScalar.of(4.373177842565599));
  }

  @Test
  void testBuild() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(BorelTannerDistribution.of(0.3, 5));
    CDF cdf = CDF.of(distribution);
    Tolerance.CHOP.requireClose(cdf.p_lessEquals(RealScalar.of(20)), RealScalar.of(0.9996946869376027));
  }

  @Test
  void testExact() throws ClassNotFoundException, IOException {
    Scalar alpha = RationalScalar.of(2, 3);
    Distribution distribution = Serialization.copy(BorelTannerDistribution.of(alpha, 7));
    CDF cdf = CDF.of(distribution);
    Tolerance.CHOP.requireClose(cdf.p_lessEquals(RealScalar.of(30)), RealScalar.of(0.8419882919812596));
  }

  @Test
  void testFails() {
    assertThrows(TensorRuntimeException.class, () -> BorelTannerDistribution.of(0, 7));
    assertThrows(TensorRuntimeException.class, () -> BorelTannerDistribution.of(1, 7));
    assertThrows(Exception.class, () -> BorelTannerDistribution.of(0.2, 0));
  }
}
