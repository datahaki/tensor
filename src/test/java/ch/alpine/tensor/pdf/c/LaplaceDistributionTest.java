// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.jet.DateObject;
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

class LaplaceDistributionTest {
  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(LaplaceDistribution.of(2, 5));
    PDF pdf = PDF.of(distribution);
    Tolerance.CHOP.requireClose(pdf.at(RealScalar.of(-3)), RealScalar.of(0.036787944117144235));
    Tolerance.CHOP.requireClose(pdf.at(RealScalar.of(+3)), RealScalar.of(0.0818730753077982));
    CDF cdf = CDF.of(distribution);
    Tolerance.CHOP.requireClose(cdf.p_lessEquals(RealScalar.of(-3)), RealScalar.of(0.18393972058572117));
    Tolerance.CHOP.requireClose(cdf.p_lessEquals(RealScalar.of(+3)), RealScalar.of(0.5906346234610091));
    InverseCDF inverseCdf = InverseCDF.of(distribution);
    Tolerance.CHOP.requireClose(inverseCdf.quantile(RealScalar.of(0.1)), RealScalar.of(-6.047189562170502));
    Tolerance.CHOP.requireClose(inverseCdf.quantile(RealScalar.of(0.9)), RealScalar.of(10.047189562170502));
    assertEquals(distribution.toString(), "LaplaceDistribution[2, 5]");
  }

  @Test
  void testRandomMeanVar() {
    Distribution distribution = LaplaceDistribution.of(3, 2);
    RandomVariate.of(distribution, 100);
    assertEquals(ExactScalarQ.require(Mean.of(distribution)), RealScalar.of(3));
    assertEquals(ExactScalarQ.require(Variance.of(distribution)), RealScalar.of(8));
  }

  @Test
  void testQuantity() {
    Distribution distribution = LaplaceDistribution.of(Quantity.of(3, "kg"), Quantity.of(2, "kg"));
    RandomVariate.of(distribution, 100);
    assertEquals(ExactScalarQ.require(Mean.of(distribution)), Quantity.of(3, "kg"));
    assertEquals(ExactScalarQ.require(Variance.of(distribution)), Quantity.of(8, "kg^2"));
  }

  @Test
  void testDateTimeScalar() {
    DateObject dateTimeScalar = DateObject.of(LocalDateTime.now());
    Distribution distribution = LaplaceDistribution.of(dateTimeScalar, Quantity.of(123, "s"));
    Scalar scalar = RandomVariate.of(distribution);
    assertInstanceOf(DateObject.class, scalar);
    PDF pdf = PDF.of(distribution);
    pdf.at(DateObject.of(LocalDateTime.now()));
    CDF cdf = CDF.of(distribution);
    Scalar p_lessEquals = cdf.p_lessEquals(DateObject.of(LocalDateTime.now()));
    Chop._01.requireClose(RationalScalar.HALF, p_lessEquals);
  }

  @Test
  void testMonotonous() {
    TestMarkovChebyshev.monotonous(LaplaceDistribution.of(3, 2));
  }

  @Test
  void testComplexFail() {
    assertThrows(ClassCastException.class, () -> LaplaceDistribution.of(ComplexScalar.of(1, 2), RealScalar.ONE));
  }

  @Test
  void testQuantityFail() {
    assertThrows(Throw.class, () -> LaplaceDistribution.of(Quantity.of(3, "m"), Quantity.of(2, "km")));
    assertThrows(Throw.class, () -> LaplaceDistribution.of(Quantity.of(0, "s"), Quantity.of(2, "m")));
    assertThrows(Throw.class, () -> LaplaceDistribution.of(Quantity.of(0, ""), Quantity.of(2, "m")));
  }

  @Test
  void testNegativeSigmaFail() {
    LaplaceDistribution.of(5, 1);
    assertThrows(Throw.class, () -> LaplaceDistribution.of(5, -1));
  }
}
