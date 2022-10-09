// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.qty.Quantity;

class ShiftedGompertzDistributionTest {
  @Test
  void testSimple() {
    Distribution distribution = ShiftedGompertzDistribution.of(2, 3);
    PDF pdf = PDF.of(distribution);
    Tolerance.CHOP.requireClose(pdf.at(RealScalar.of(0.3)), RealScalar.of(0.49789976922337764));
    assertEquals(pdf.at(RealScalar.of(-1)), RealScalar.ZERO);
    CDF cdf = CDF.of(distribution);
    Tolerance.CHOP.requireClose(cdf.p_lessThan(RealScalar.of(1.3)), RealScalar.of(0.7408202479584333));
    assertEquals(cdf.p_lessThan(RealScalar.of(-1.3)), RealScalar.ZERO);
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    Scalar quantile = inverseCDF.quantile(RealScalar.of(0.6));
    Tolerance.CHOP.requireClose(quantile, RealScalar.of(1.037510735992861));
    assertTrue(distribution.toString().startsWith("ShiftedGompertzDistribution["));
    assertEquals(inverseCDF.quantile(RealScalar.ONE), DoubleScalar.POSITIVE_INFINITY);
  }

  @Test
  void testQuantity() {
    Distribution distribution = ShiftedGompertzDistribution.of(Quantity.of(2, "s^-1"), RealScalar.of(3));
    PDF pdf = PDF.of(distribution);
    Tolerance.CHOP.requireClose(pdf.at(Quantity.of(0.3, "s")), Quantity.of(0.49789976922337764, "s^-1"));
    CDF cdf = CDF.of(distribution);
    Tolerance.CHOP.requireClose(cdf.p_lessThan(Quantity.of(1.3, "s")), RealScalar.of(0.7408202479584333));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    Scalar quantile = inverseCDF.quantile(RealScalar.of(0.6));
    Tolerance.CHOP.requireClose(quantile, Quantity.of(1.037510735992861, "s"));
  }

  @Test
  void testFail() {
    assertThrows(Exception.class, () -> ShiftedGompertzDistribution.of(2, 0));
  }
}
