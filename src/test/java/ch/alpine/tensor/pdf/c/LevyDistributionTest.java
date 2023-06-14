// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;

class LevyDistributionTest {
  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(LevyDistribution.of(-1.3, 2.5));
    PDF pdf = PDF.of(distribution);
    Tolerance.CHOP.requireClose(pdf.at(RealScalar.of(3)), RealScalar.of(0.052896750155658534));
    CDF cdf = CDF.of(distribution);
    Tolerance.CHOP.requireClose(cdf.p_lessThan(RealScalar.of(3)), RealScalar.of(0.4457659079148423));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    Scalar x = inverseCDF.quantile(RealScalar.of(0.75));
    Tolerance.CHOP.requireClose(x, RealScalar.of(23.323010804560926));
    assertTrue(distribution.toString().startsWith("LevyDistribution["));
    assertEquals(Mean.of(distribution), DoubleScalar.POSITIVE_INFINITY);
    assertEquals(Variance.of(distribution), DoubleScalar.POSITIVE_INFINITY);
    RandomVariate.of(distribution, 100);
  }

  @Test
  void testQuantity() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(LevyDistribution.of(Quantity.of(-1.3, "m"), Quantity.of(2.5, "m")));
    PDF pdf = PDF.of(distribution);
    Tolerance.CHOP.requireClose(pdf.at(Quantity.of(3, "m")), Quantity.of(0.052896750155658534, "m^-1"));
    CDF cdf = CDF.of(distribution);
    Tolerance.CHOP.requireClose(cdf.p_lessThan(Quantity.of(3, "m")), RealScalar.of(0.4457659079148423));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    Scalar x = inverseCDF.quantile(RealScalar.of(0.75));
    Tolerance.CHOP.requireClose(x, Quantity.of(23.323010804560926, "m"));
    assertTrue(distribution.toString().startsWith("LevyDistribution["));
    assertEquals(Mean.of(distribution), Quantity.of(DoubleScalar.POSITIVE_INFINITY, "m"));
    assertEquals(Variance.of(distribution), Quantity.of(DoubleScalar.POSITIVE_INFINITY, "m"));
    RandomVariate.of(distribution, 100);
  }
}
