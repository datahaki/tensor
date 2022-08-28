// code by jph
package ch.alpine.tensor.pdf.d;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.red.Mean;

class WaringYuleDistributionTest {
  @Test
  void testPdf() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(WaringYuleDistribution.of(1.4));
    Scalar scalar = PDF.of(distribution).at(RealScalar.TWO);
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(0.0779857397504456));
    Scalar mean = Mean.of(distribution);
    Tolerance.CHOP.requireClose(mean, RealScalar.of(2.5));
    assertTrue(distribution.toString().startsWith("WaringYuleDistribution["));
  }

  @Test
  void testMean() {
    Distribution distribution = WaringYuleDistribution.of(0.4);
    Scalar scalar = PDF.of(distribution).at(Pi.VALUE);
    Tolerance.CHOP.requireZero(scalar);
    Scalar mean = Mean.of(distribution);
    assertFalse(FiniteScalarQ.of(mean));
  }

  @Test
  void testCdfLe() {
    Distribution distribution = WaringYuleDistribution.of(1.4);
    Scalar scalar = CDF.of(distribution).p_lessEquals(RealScalar.of(130));
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(0.9986681412708701));
  }

  @Test
  void testCdfLt() {
    Distribution distribution = WaringYuleDistribution.of(1.4);
    Scalar scalar = CDF.of(distribution).p_lessThan(RealScalar.of(131));
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(0.9986681412708701));
  }
}
