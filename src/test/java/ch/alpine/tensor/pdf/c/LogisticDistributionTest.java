// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.IOException;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.ExactScalarQ;
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
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class LogisticDistributionTest extends TestCase {
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
    AssertFail.of(() -> inverseCDF.quantile(RealScalar.of(-0.1)));
    AssertFail.of(() -> inverseCDF.quantile(RealScalar.of(+1.1)));
  }

  public void testRandomMeanVar() {
    Distribution distribution = LogisticDistribution.of(3, 2);
    RandomVariate.of(distribution, 100);
    Tolerance.CHOP.requireClose(ExactScalarQ.require(Mean.of(distribution)), RealScalar.of(3));
    Tolerance.CHOP.requireClose(Variance.of(distribution), RealScalar.of(13.15947253478581));
    assertEquals(distribution.toString(), "LogisticDistribution[3, 2]");
  }

  public void testQuantity() {
    Distribution distribution = LogisticDistribution.of(Quantity.of(2, "m"), Quantity.of(3, "m"));
    Scalar scalar = Variance.of(distribution);
    QuantityMagnitude.singleton("m^2").apply(scalar);
    Scalar lo = InverseCDF.of(distribution).quantile(RealScalar.ZERO);
    assertEquals(lo, Quantity.of(DoubleScalar.NEGATIVE_INFINITY, "m"));
  }

  public void testNullFail() {
    AssertFail.of(() -> LogisticDistribution.of(null, RealScalar.ONE));
    AssertFail.of(() -> LogisticDistribution.of(RealScalar.ONE, null));
  }

  public void testZeroFail() {
    AssertFail.of(() -> LogisticDistribution.of(RealScalar.ONE, RealScalar.ZERO));
  }

  public void testComplexFail() {
    AssertFail.of(() -> LogisticDistribution.of(ComplexScalar.of(1, 2), RealScalar.ONE));
  }

  public void testQuantityFail() {
    AssertFail.of(() -> LogisticDistribution.of(Quantity.of(3, "m"), Quantity.of(2, "km")));
    AssertFail.of(() -> LogisticDistribution.of(Quantity.of(0, "s"), Quantity.of(2, "m")));
    AssertFail.of(() -> LogisticDistribution.of(Quantity.of(0, ""), Quantity.of(2, "m")));
  }

  public void testNegativeSigmaFail() {
    LogisticDistribution.of(5, 1);
    AssertFail.of(() -> LogisticDistribution.of(5, -1));
  }
}
