// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
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
  void testSimple() {
    Distribution distribution = Serialization.copy(ChiSquareDistribution.of(2.3));
    PDF pdf = PDF.of(distribution);
    Scalar scalar = pdf.at(RealScalar.of(1.4));
    Chop._12.requireClose(scalar, RealScalar.of(0.2522480844885552));
    assertEquals(Mean.of(distribution), RealScalar.of(2.3));
    assertEquals(Variance.of(distribution), RealScalar.of(2.3 + 2.3));
    assertEquals(pdf.at(RealScalar.of(-1.4)), RealScalar.ZERO);
    assertThrows(Exception.class, () -> pdf.at(Quantity.of(2, "m")));
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
    // assertThrows(UnsupportedOperationException.class, () -> cdf.quantile(Rational.HALF));
  }

  @RepeatedTest(4)
  void testRandomFails(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Distribution distribution = ChiSquareDistribution.of(n);
    Tensor r1 = RandomVariate.of(distribution, new Random(3), 10);
    Tensor r2 = RandomVariate.of(distribution, new Random(3), 10);
    Tolerance.CHOP.requireClose(r1, r2);
  }

  @Test
  void testFails() {
    assertThrows(Exception.class, () -> ChiSquareDistribution.of(0));
    assertThrows(Exception.class, () -> ChiSquareDistribution.of(-2.3));
    assertThrows(Exception.class, () -> ChiSquareDistribution.of(Quantity.of(2, "m")));
  }

  @Test
  void testToString() {
    Distribution distribution = ChiSquareDistribution.of(RealScalar.of(3));
    assertEquals(distribution.toString(), "ChiSquareDistribution[3]");
  }
}
