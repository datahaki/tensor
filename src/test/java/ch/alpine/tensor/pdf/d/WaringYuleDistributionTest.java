// code by jph
package ch.alpine.tensor.pdf.d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.chq.FiniteScalarQ;
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

class WaringYuleDistributionTest {
  @Test
  void testPdf() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(WaringYuleDistribution.of(1.4, 1));
    PDF pdf = PDF.of(distribution);
    Scalar scalar = pdf.at(RealScalar.TWO);
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(0.0779857397504456));
    Scalar mean = Mean.of(distribution);
    Tolerance.CHOP.requireClose(mean, RealScalar.of(2.5));
    assertTrue(distribution.toString().startsWith("WaringYuleDistribution["));
    Tolerance.CHOP.requireZero(pdf.at(RealScalar.ONE.negate()));
  }

  @Test
  void testMean() {
    Distribution distribution = WaringYuleDistribution.of(0.4, 1);
    Scalar scalar = PDF.of(distribution).at(Pi.VALUE);
    Tolerance.CHOP.requireZero(scalar);
    Scalar mean = Mean.of(distribution);
    assertFalse(FiniteScalarQ.of(mean));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    assertEquals(inverseCDF.quantile(RealScalar.of(0.5)), RealScalar.of(3));
    assertEquals(inverseCDF.quantile(RealScalar.of(0.99)), RealScalar.of(74152));
    assertEquals(inverseCDF.quantile(RealScalar.ONE), DoubleScalar.POSITIVE_INFINITY);
    RandomVariate.of(distribution, 100);
    assertThrows(Exception.class, () -> Variance.of(distribution));
  }

  @Test
  void testCdfLe() {
    Distribution distribution = WaringYuleDistribution.of(1.4, 1);
    Scalar scalar = CDF.of(distribution).p_lessEquals(RealScalar.of(130));
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(0.9986681412708701));
    Scalar s2 = CDF.of(distribution).p_lessEquals(RealScalar.of(130.1));
    Tolerance.CHOP.requireClose(s2, RealScalar.of(0.9986681412708701));
  }

  @Test
  void testCdfLt() {
    Distribution distribution = WaringYuleDistribution.of(1.4, 1);
    CDF cdf = CDF.of(distribution);
    Scalar scalar = cdf.p_lessThan(RealScalar.of(131));
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(0.9986681412708701));
    assertEquals(cdf.p_lessThan(RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(cdf.p_lessEquals(RealScalar.of(-1e-100)), RealScalar.ZERO);
  }

  @Test
  void testFails() {
    assertThrows(Exception.class, () -> WaringYuleDistribution.of(0, 1));
    assertThrows(Exception.class, () -> WaringYuleDistribution.of(1, 0));
  }
}
