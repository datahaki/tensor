// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;

class BirnbaumSaundersDistributionTest {
  @Test
  void test() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(BirnbaumSaundersDistribution.of(0.3, 0.8));
    PDF pdf = PDF.of(distribution);
    Tolerance.CHOP.requireClose(pdf.at(RealScalar.ZERO), RealScalar.ZERO);
    Scalar p = pdf.at(RealScalar.of(1.4));
    Tolerance.CHOP.requireClose(p, RealScalar.of(0.8858018743857796));
    Tolerance.CHOP.requireClose(Mean.of(distribution), RealScalar.of(1.30625));
    Tolerance.CHOP.requireClose(Variance.of(distribution), RealScalar.of(0.1564453125));
    CDF cdf = CDF.of(distribution);
    Tolerance.CHOP.requireClose(cdf.p_lessEquals(RealScalar.ZERO), RealScalar.ZERO);
    Scalar cp = cdf.p_lessEquals(RealScalar.of(1.23));
    Tolerance.CHOP.requireClose(cp, RealScalar.of(0.47856112217528485));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    Scalar quantile = inverseCDF.quantile(RealScalar.of(0.78));
    Tolerance.CHOP.requireClose(quantile, RealScalar.of(1.5750493692432221));
    RandomVariate.of(distribution, 30);
    assertTrue(distribution.toString().startsWith("BirnbaumSaundersDistribution["));
  }

  @Test
  void testMonotonous() {
    TestMarkovChebyshev.monotonous(BirnbaumSaundersDistribution.of(0.3, 0.8));
  }

  @Test
  void testFails() {
    assertThrows(Throw.class, () -> BirnbaumSaundersDistribution.of(0, 7));
    assertThrows(Throw.class, () -> BirnbaumSaundersDistribution.of(1, 0));
  }
}
