// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;

class WeibullDistributionTest {
  @Test
  void testXDF() {
    Distribution distribution = WeibullDistribution.of(1, 2);
    PDF pdf = PDF.of(distribution);
    Tolerance.CHOP.requireClose(pdf.at(RealScalar.of(3)), RealScalar.of(0.11156508007421491));
    Tolerance.CHOP.requireClose(pdf.at(RealScalar.of(-1)), RealScalar.of(0));
    CDF cdf = CDF.of(distribution);
    Tolerance.CHOP.requireClose(cdf.p_lessEquals(RealScalar.of(3)), RealScalar.of(0.7768698398515702));
    Tolerance.CHOP.requireClose(cdf.p_lessEquals(RealScalar.of(-1)), RealScalar.of(0));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    Scalar quantile = inverseCDF.quantile(RealScalar.of(0.4));
    Tolerance.CHOP.requireClose(quantile, RealScalar.of(1.0216512475319814));
  }

  @Test
  void testExpectation() throws ClassNotFoundException, IOException {
    Distribution distribution = WeibullDistribution.of(1.2, 2.3);
    Serialization.copy(distribution);
    assertTrue(distribution.toString().startsWith("WeibullDistribution["));
    Tolerance.CHOP.requireClose(Mean.of(distribution), RealScalar.of(2.1635084739905746));
    Tolerance.CHOP.requireClose(Variance.of(distribution), RealScalar.of(3.2784354158217064));
  }

  @Test
  void testFails() {
    assertThrows(Exception.class, () -> WeibullDistribution.of(0, 1));
    assertThrows(Exception.class, () -> WeibullDistribution.of(1, 0));
    assertThrows(Exception.class, () -> WeibullDistribution.of(1, -2));
    assertThrows(Exception.class, () -> WeibullDistribution.of(-1, 2));
    assertThrows(Exception.class, () -> WeibullDistribution.of(-1, -2));
  }
}
