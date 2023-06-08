// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;

class InverseGaussianDistributionTest {
  @Test
  void testSimple() {
    Distribution distribution = InverseGaussianDistribution.of(2.3, 1.4);
    PDF pdf = PDF.of(distribution);
    Scalar scalar = pdf.at(RealScalar.of(4.5));
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(0.042888929007911435));
    CDF cdf = CDF.of(distribution);
    Scalar p = cdf.p_lessThan(RealScalar.of(3.5));
    Tolerance.CHOP.requireClose(p, RealScalar.of(0.816346681938743));
    Tolerance.CHOP.requireClose(Mean.of(distribution), RealScalar.of(2.3));
    Scalar var = RealScalar.of(8.690714285714284);
    Tolerance.CHOP.requireClose(Variance.of(distribution), var);
    assertTrue(distribution.toString().startsWith("InverseGaussianDistribution["));
  }

  @Test
  void testQuantity() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy( //
        InverseGaussianDistribution.of(Quantity.of(2.3, "m"), Quantity.of(1.4, "m")));
    PDF pdf = PDF.of(distribution);
    {
      Scalar scalar = pdf.at(Quantity.of(4.5, "m"));
      Tolerance.CHOP.requireClose(scalar, Quantity.of(0.042888929007911435, "m^-1"));
    }
    {
      Scalar scalar = pdf.at(Quantity.of(-1, "m"));
      Tolerance.CHOP.requireClose(scalar, Quantity.of(0, "m^-1"));
    }
    {
      CDF cdf = CDF.of(distribution);
      Scalar p = cdf.p_lessThan(Quantity.of(3.5, "m"));
      Tolerance.CHOP.requireClose(p, RealScalar.of(0.816346681938743));
    }
  }

  @Test
  void testToString() {
    Distribution distribution = InverseGaussianDistribution.of(2, 3);
    assertEquals(distribution.toString(), "InverseGaussianDistribution[2, 3]");
  }

  @Test
  void testFails() {
    assertThrows(Exception.class, () -> InverseGaussianDistribution.of(0, 1));
    assertThrows(Exception.class, () -> InverseGaussianDistribution.of(1, 0));
  }
}
