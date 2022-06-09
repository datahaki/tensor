// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.jet.DateTimeScalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Chop;

class LogisticDistributionTest {
  @Test
  public void testSimple() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(LogisticDistribution.of(2, 3));
    PDF pdf = PDF.of(distribution);
    Tolerance.CHOP.requireClose(pdf.at(RealScalar.of(1.2)), RealScalar.of(0.0818692348913425));
    CDF cdf = CDF.of(distribution);
    Scalar x = RealScalar.of(3.4);
    Scalar p_lessEquals = cdf.p_lessEquals(x);
    Tolerance.CHOP.requireClose(p_lessEquals, RealScalar.of(0.6145944982675495));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    Scalar quantile = inverseCDF.quantile(p_lessEquals);
    Tolerance.CHOP.requireClose(quantile, x);
    assertThrows(TensorRuntimeException.class, () -> inverseCDF.quantile(RealScalar.of(-0.1)));
    assertThrows(TensorRuntimeException.class, () -> inverseCDF.quantile(RealScalar.of(+1.1)));
  }

  @Test
  public void testRandomMeanVar() {
    Distribution distribution = LogisticDistribution.of(3, 2);
    RandomVariate.of(distribution, 100);
    Tolerance.CHOP.requireClose(ExactScalarQ.require(Mean.of(distribution)), RealScalar.of(3));
    Tolerance.CHOP.requireClose(Variance.of(distribution), RealScalar.of(13.15947253478581));
    assertEquals(distribution.toString(), "LogisticDistribution[3, 2]");
  }

  @Test
  public void testQuantity() {
    Distribution distribution = LogisticDistribution.of(Quantity.of(2, "m"), Quantity.of(3, "m"));
    Scalar scalar = Variance.of(distribution);
    QuantityMagnitude.singleton("m^2").apply(scalar);
    Scalar lo = InverseCDF.of(distribution).quantile(RealScalar.ZERO);
    assertEquals(lo, Quantity.of(DoubleScalar.NEGATIVE_INFINITY, "m"));
  }

  @Test
  public void testDateTimeScalar() {
    DateTimeScalar dateTimeScalar = DateTimeScalar.of(LocalDateTime.now());
    Scalar durationScalar = Quantity.of(123, "s");
    Distribution distribution = LogisticDistribution.of(dateTimeScalar, durationScalar);
    Scalar scalar = RandomVariate.of(distribution);
    assertInstanceOf(DateTimeScalar.class, scalar);
    PDF pdf = PDF.of(distribution);
    pdf.at(DateTimeScalar.of(LocalDateTime.now()));
    CDF cdf = CDF.of(distribution);
    Scalar p_lessEquals = cdf.p_lessEquals(DateTimeScalar.of(LocalDateTime.now()));
    Chop._01.requireClose(RationalScalar.HALF, p_lessEquals);
  }

  @Test
  public void testNullFail() {
    assertThrows(NullPointerException.class, () -> LogisticDistribution.of(null, RealScalar.ONE));
    assertThrows(NullPointerException.class, () -> LogisticDistribution.of(RealScalar.ONE, null));
  }

  @Test
  public void testZeroFail() {
    assertThrows(TensorRuntimeException.class, () -> LogisticDistribution.of(RealScalar.ONE, RealScalar.ZERO));
  }

  @Test
  public void testComplexFail() {
    assertThrows(ClassCastException.class, () -> LogisticDistribution.of(ComplexScalar.of(1, 2), RealScalar.ONE));
    assertThrows(ClassCastException.class, () -> LogisticDistribution.of(RealScalar.ONE, ComplexScalar.of(1, 2)));
  }

  @Test
  public void testQuantityFail() {
    assertThrows(TensorRuntimeException.class, () -> LogisticDistribution.of(Quantity.of(3, "m"), Quantity.of(2, "km")));
    assertThrows(TensorRuntimeException.class, () -> LogisticDistribution.of(Quantity.of(0, "s"), Quantity.of(2, "m")));
    assertThrows(TensorRuntimeException.class, () -> LogisticDistribution.of(Quantity.of(0, ""), Quantity.of(2, "m")));
  }

  @Test
  public void testNegativeSigmaFail() {
    LogisticDistribution.of(5, 1);
    assertThrows(TensorRuntimeException.class, () -> LogisticDistribution.of(5, -1));
  }
}
