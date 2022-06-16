// code by jph
package ch.alpine.tensor.pdf.d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.sca.Clips;

class DiscreteUniformDistributionTest {
  @Test
  void testPdf() throws ClassNotFoundException, IOException {
    Distribution distribution = //
        Serialization.copy(DiscreteUniformDistribution.of(RealScalar.of(3), RealScalar.of(11)));
    PDF pdf = PDF.of(distribution);
    Scalar prob = pdf.at(RealScalar.of(4));
    assertEquals(prob, RationalScalar.of(1, 10 - 3 + 1));
    assertEquals(pdf.at(RealScalar.of(4)), pdf.at(RealScalar.of(8)));
    assertEquals(pdf.at(RealScalar.of(2)), RealScalar.ZERO);
    assertEquals(pdf.at(RealScalar.of(2)), pdf.at(RealScalar.of(11)));
    assertEquals(pdf.at(RealScalar.of(10)), RationalScalar.of(1, 10 - 3 + 1));
    assertEquals(pdf.at(RealScalar.of(11)), RealScalar.ZERO);
  }

  @Test
  void testLessThan() {
    Distribution distribution = DiscreteUniformDistribution.of(RealScalar.of(3), RealScalar.of(11));
    CDF cdf = CDF.of(distribution);
    assertEquals(cdf.p_lessThan(RealScalar.of(2)), RationalScalar.of(0, 10 - 3 + 1));
    assertEquals(cdf.p_lessThan(RealScalar.of(3)), RationalScalar.of(0, 10 - 3 + 1));
    assertEquals(cdf.p_lessThan(RealScalar.of(3.9)), RationalScalar.of(1, 10 - 3 + 1));
    assertEquals(cdf.p_lessThan(RealScalar.of(4)), RationalScalar.of(1, 10 - 3 + 1));
    assertEquals(cdf.p_lessThan(RealScalar.of(4.1)), RationalScalar.of(2, 10 - 3 + 1));
    assertEquals(cdf.p_lessThan(RealScalar.of(5)), RationalScalar.of(2, 10 - 3 + 1));
    assertEquals(cdf.p_lessThan(RealScalar.of(10)), RationalScalar.of(7, 10 - 3 + 1));
    assertEquals(cdf.p_lessThan(RealScalar.of(11)), RationalScalar.of(8, 10 - 3 + 1));
  }

  @Test
  void testLessEquals() {
    Distribution distribution = DiscreteUniformDistribution.of(RealScalar.of(3), RealScalar.of(11));
    CDF cdf = CDF.of(distribution);
    assertEquals(cdf.p_lessEquals(RealScalar.of(2)), RationalScalar.of(0, 10 - 3 + 1));
    assertEquals(cdf.p_lessEquals(RealScalar.of(3)), RationalScalar.of(1, 10 - 3 + 1));
    assertEquals(cdf.p_lessEquals(RealScalar.of(4)), RationalScalar.of(2, 10 - 3 + 1));
    assertEquals(cdf.p_lessEquals(RealScalar.of(4.1)), RationalScalar.of(2, 10 - 3 + 1));
    assertEquals(cdf.p_lessEquals(RealScalar.of(5)), RationalScalar.of(3, 10 - 3 + 1));
    assertEquals(cdf.p_lessEquals(RealScalar.of(10)), RationalScalar.of(8, 10 - 3 + 1));
    assertEquals(cdf.p_lessEquals(RealScalar.of(11)), RationalScalar.of(8, 10 - 3 + 1));
  }

  @Test
  void testEqualMinMax() {
    DiscreteUniformDistribution.of(RealScalar.of(3), RealScalar.of(4));
    DiscreteUniformDistribution.of(10, 11);
  }

  @Test
  void testInverseCDF() {
    InverseCDF inverseCDF = InverseCDF.of(DiscreteUniformDistribution.of(0, 10));
    Scalar s = inverseCDF.quantile(RationalScalar.of(1, 2));
    assertTrue(Clips.interval(4, 5).isInside(s));
    assertEquals(inverseCDF.quantile(RealScalar.of(0.9999999)), RealScalar.of(9));
    assertEquals(inverseCDF.quantile(RealScalar.of(1)), RealScalar.of(9));
  }

  @Test
  void testToString() {
    Distribution distribution = DiscreteUniformDistribution.of(3, 10);
    assertEquals(distribution.toString(), "DiscreteUniformDistribution[3, 10]");
  }

  @Test
  void testFailQuantile() {
    Distribution distribution = DiscreteUniformDistribution.of(3, 10);
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    assertThrows(TensorRuntimeException.class, () -> inverseCDF.quantile(RealScalar.of(-0.1)));
    assertThrows(TensorRuntimeException.class, () -> inverseCDF.quantile(RealScalar.of(1.1)));
  }

  @Test
  void testFailsOrder() {
    assertThrows(IllegalArgumentException.class, () -> DiscreteUniformDistribution.of(RealScalar.of(3), RealScalar.of(2)));
    assertThrows(IllegalArgumentException.class, () -> DiscreteUniformDistribution.of(3, 2));
    assertThrows(IllegalArgumentException.class, () -> DiscreteUniformDistribution.of(3, 3));
  }

  @Test
  void testFailsInt() {
    assertThrows(TensorRuntimeException.class, () -> DiscreteUniformDistribution.of(RealScalar.of(3), RealScalar.of(4.5)));
  }

  @Test
  void testRandomVariate() {
    AbstractDiscreteDistribution distribution = //
        (AbstractDiscreteDistribution) DiscreteUniformDistribution.of(10, 100);
    assertEquals(distribution.quantile(RealScalar.of(Math.nextDown(1.0))), RealScalar.of(99));
    assertEquals(distribution.quantile(RealScalar.of(0)), RealScalar.of(10));
  }
}
