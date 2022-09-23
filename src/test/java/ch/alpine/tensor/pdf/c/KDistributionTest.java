// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Chop;

class KDistributionTest {
  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    KDistribution kDistribution = (KDistribution) Serialization.copy(KDistribution.of(1, 2.3));
    PDF pdf = PDF.of(kDistribution);
    Scalar p = pdf.at(RealScalar.of(0.2));
    Chop._08.requireClose(p, RealScalar.of(0.518777716548126));
    assertEquals(pdf.at(RealScalar.ZERO), RealScalar.ZERO);
    CDF cdf = CDF.of(kDistribution);
    Scalar c = cdf.p_lessEquals(RealScalar.of(0.3));
    Chop._08.requireClose(c, RealScalar.of(0.1243115934496789));
    Scalar max = kDistribution.support.max();
    Scalar scalar = pdf.at(max);
    Chop._20.requireZero(scalar);
    assertEquals(cdf.p_lessEquals(max), RealScalar.ONE);
  }

  @Test
  void testInverseCDF() {
    Distribution distribution = KDistribution.of(1, 2.3);
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    Scalar quantile = inverseCDF.quantile(RationalScalar.HALF);
    Chop._10.requireClose(quantile, RealScalar.of(0.9532822409824206)); // Mathematica
    Scalar quantile2 = inverseCDF.quantile(RealScalar.ONE);
    assertEquals(quantile2, DoubleScalar.POSITIVE_INFINITY);
  }

  @Test
  void testRandom() {
    Distribution distribution = KDistribution.of(1, 2.3);
    RandomVariate.of(distribution, 30);
    assertTrue(distribution.toString().startsWith("KDistribution["));
  }

  @Test
  void testMean() {
    Distribution distribution = KDistribution.of(2, 8);
    Scalar mean = Mean.of(distribution);
    Tolerance.CHOP.requireClose(mean, Pi.VALUE.multiply(RationalScalar.of(3, 4)));
    Scalar variance = Variance.of(distribution);
    Tolerance.CHOP.requireClose(variance, RealScalar.of(2.448347524387236));
  }

  @Test
  void testMean2() {
    Distribution distribution = KDistribution.of(0.2, 1.3);
    Scalar mean = Mean.of(distribution);
    Tolerance.CHOP.requireClose(mean, RealScalar.of(0.6388550270417801));
  }

  @Test
  void testSignFails() {
    assertThrows(Exception.class, () -> KDistribution.of(0, 3));
    assertThrows(Exception.class, () -> KDistribution.of(2, 0));
  }

  @Test
  void testNullFails() {
    assertThrows(Exception.class, () -> KDistribution.of(null, 3));
    assertThrows(Exception.class, () -> KDistribution.of(2, null));
    assertThrows(Exception.class, () -> KDistribution.of(null, Pi.VALUE));
    assertThrows(Exception.class, () -> KDistribution.of(Pi.VALUE, null));
  }
}
