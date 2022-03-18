// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RealScalar;
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
import ch.alpine.tensor.usr.AssertFail;

public class LaplaceDistributionTest {
  @Test
  public void testSimple() throws ClassNotFoundException, IOException {
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
  public void testRandomMeanVar() {
    Distribution distribution = LaplaceDistribution.of(3, 2);
    RandomVariate.of(distribution, 100);
    assertEquals(ExactScalarQ.require(Mean.of(distribution)), RealScalar.of(3));
    assertEquals(ExactScalarQ.require(Variance.of(distribution)), RealScalar.of(8));
  }

  @Test
  public void testQuantity() {
    Distribution distribution = LaplaceDistribution.of(Quantity.of(3, "kg"), Quantity.of(2, "kg"));
    RandomVariate.of(distribution, 100);
    assertEquals(ExactScalarQ.require(Mean.of(distribution)), Quantity.of(3, "kg"));
    assertEquals(ExactScalarQ.require(Variance.of(distribution)), Quantity.of(8, "kg^2"));
  }

  @Test
  public void testComplexFail() {
    AssertFail.of(() -> LaplaceDistribution.of(ComplexScalar.of(1, 2), RealScalar.ONE));
  }

  @Test
  public void testQuantityFail() {
    AssertFail.of(() -> LaplaceDistribution.of(Quantity.of(3, "m"), Quantity.of(2, "km")));
    AssertFail.of(() -> LaplaceDistribution.of(Quantity.of(0, "s"), Quantity.of(2, "m")));
    AssertFail.of(() -> LaplaceDistribution.of(Quantity.of(0, ""), Quantity.of(2, "m")));
  }

  @Test
  public void testNegativeSigmaFail() {
    LaplaceDistribution.of(5, 1);
    AssertFail.of(() -> LaplaceDistribution.of(5, -1));
  }
}
