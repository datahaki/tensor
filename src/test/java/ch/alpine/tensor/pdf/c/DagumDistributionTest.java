// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Sign;

class DagumDistributionTest {
  @Test
  void testCdf() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(DagumDistribution.of(0.2, 0.3, 0.6));
    CDF cdf = CDF.of(distribution);
    Scalar scalar = cdf.p_lessThan(RealScalar.of(3));
    Chop._12.requireClose(scalar, RealScalar.of(0.9083561837802137));
  }

  @Test
  void testPdf() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(DagumDistribution.of(0.2, 0.3, 0.6));
    PDF pdf = PDF.of(distribution);
    Scalar scalar = pdf.at(RealScalar.of(2));
    Chop._12.requireClose(scalar, RealScalar.of(0.011083755340304258));
  }

  @Test
  void testInverseCdf() {
    Distribution distribution = DagumDistribution.of(0.2, 0.3, 0.6);
    InverseCDF cdf = InverseCDF.of(distribution);
    Scalar scalar = cdf.quantile(RealScalar.of(0.75));
    Chop._12.requireClose(scalar, RealScalar.of(0.012246219782933493));
  }

  @Test
  void testMean() {
    Distribution distribution = DagumDistribution.of(2.3, 1.2, 0.7);
    Scalar scalar = Mean.of(distribution);
    Chop._12.requireClose(scalar, RealScalar.of(7.579940034748095));
  }

  @Test
  void testVarianceFail() {
    DagumDistribution distribution = (DagumDistribution) DagumDistribution.of(2.3, 1.2, 0.7);
    assertThrows(UnsupportedOperationException.class, () -> distribution.variance());
  }

  @Test
  void testMeanIndeterminate() {
    Distribution distribution = DagumDistribution.of(2.3, 1, 0.7);
    Scalar scalar = Mean.of(distribution);
    assertFalse(FiniteScalarQ.of(scalar));
  }

  @Test
  void testRandom() {
    Distribution distribution = DagumDistribution.of(0.2, 0.3, 0.6);
    Scalar mean = Mean.ofVector(RandomVariate.of(distribution, 100));
    Sign.requirePositive(mean);
  }

  @Test
  void testString() {
    Distribution distribution = DagumDistribution.of(1, 2, 3);
    assertEquals(distribution.toString(), "DagumDistribution[1, 2, 3]");
  }

  @Test
  void testFailNonPositive() {
    assertThrows(TensorRuntimeException.class, () -> DagumDistribution.of(0, 2, 3));
    assertThrows(TensorRuntimeException.class, () -> DagumDistribution.of(1, 0, 3));
    assertThrows(TensorRuntimeException.class, () -> DagumDistribution.of(1, 2, 0));
  }

  @Test
  void testFailNegative() {
    assertThrows(TensorRuntimeException.class, () -> DagumDistribution.of(-1, 2, 3));
    assertThrows(TensorRuntimeException.class, () -> DagumDistribution.of(1, -1, 3));
    assertThrows(TensorRuntimeException.class, () -> DagumDistribution.of(1, 2, -1));
  }
}
