// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Chop;

class ChiSquareDistributionTest {
  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(ChiSquareDistribution.of(2.3));
    PDF pdf = PDF.of(distribution);
    Scalar scalar = pdf.at(RealScalar.of(1.4));
    Chop._12.requireClose(scalar, RealScalar.of(0.2522480844885552));
    assertEquals(Mean.of(distribution), RealScalar.of(2.3));
    assertEquals(Variance.of(distribution), RealScalar.of(2.3 + 2.3));
    assertEquals(pdf.at(RealScalar.of(-1.4)), RealScalar.ZERO);
    assertThrows(Throw.class, () -> pdf.at(Quantity.of(2, "m")));
  }

  @Test
  void testMonotonous() {
    TestMarkovChebyshev.monotonous(ChiSquareDistribution.of(1.3));
  }

  @Test
  void testCdfFails() {
    assertThrows(Exception.class, () -> CDF.of(ChiSquareDistribution.of(3)));
    // assertThrows(UnsupportedOperationException.class, () -> cdf.p_lessThan(RealScalar.TWO));
    // assertThrows(UnsupportedOperationException.class, () -> cdf.p_lessEquals(RealScalar.TWO));
  }

  @Test
  void testInverseCdfFails() {
    assertThrows(Exception.class, () -> InverseCDF.of(ChiSquareDistribution.of(3)));
    // assertThrows(UnsupportedOperationException.class, () -> cdf.quantile(RationalScalar.HALF));
  }

  @Test
  void testRandomFails() {
    Distribution distribution = ChiSquareDistribution.of(3);
    assertThrows(UnsupportedOperationException.class, () -> RandomVariate.of(distribution));
  }

  @Test
  void testFails() {
    assertThrows(Throw.class, () -> ChiSquareDistribution.of(0));
    assertThrows(Throw.class, () -> ChiSquareDistribution.of(-2.3));
    assertThrows(Throw.class, () -> ChiSquareDistribution.of(Quantity.of(2, "m")));
  }

  @Test
  void testToString() {
    Distribution distribution = ChiSquareDistribution.of(RationalScalar.of(3, 2));
    assertEquals(distribution.toString(), "ChiSquareDistribution[3/2]");
  }
}
