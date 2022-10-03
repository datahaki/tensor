// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;

class ShiftedGompertzDistributionTest {
  @Test
  void test() {
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
  }
}
