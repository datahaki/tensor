// code by jph
package ch.alpine.tensor.pdf;

import java.util.Map;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Tally;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class CategoricalDistributionTest extends TestCase {
  public void testPDF() {
    Distribution distribution = CategoricalDistribution.fromUnscaledPDF(Tensors.vector(0, 9, 1));
    PDF pdf = PDF.of(distribution);
    assertEquals(pdf.at(RealScalar.of(0)), RealScalar.ZERO);
    assertEquals(pdf.at(RealScalar.of(1)), RationalScalar.of(9, 10));
    assertEquals(pdf.at(RealScalar.of(2)), RationalScalar.of(1, 10));
  }

  public void testCDF() {
    Distribution distribution = CategoricalDistribution.fromUnscaledPDF(Tensors.vector(0, 9, 1));
    CDF pdf = CDF.of(distribution);
    assertEquals(pdf.p_lessEquals(RealScalar.of(-0.1)), RealScalar.ZERO);
    assertEquals(pdf.p_lessEquals(RealScalar.of(0)), RealScalar.ZERO);
    assertEquals(pdf.p_lessEquals(RealScalar.of(0.1)), RealScalar.ZERO);
    assertEquals(pdf.p_lessEquals(RealScalar.of(1)), RationalScalar.of(9, 10));
    assertEquals(pdf.p_lessEquals(RealScalar.of(1.1)), RationalScalar.of(9, 10));
    assertEquals(pdf.p_lessEquals(RealScalar.of(2)), RationalScalar.of(10, 10));
    assertEquals(pdf.p_lessEquals(RealScalar.of(3)), RationalScalar.of(10, 10));
  }

  public void testCDF2() {
    Distribution distribution = CategoricalDistribution.fromUnscaledPDF(Tensors.vector(0, 9, 1));
    CDF pdf = CDF.of(distribution);
    assertEquals(pdf.p_lessThan(RealScalar.of(-0.1)), RealScalar.ZERO);
    assertEquals(pdf.p_lessThan(RealScalar.of(0)), RealScalar.ZERO);
    assertEquals(pdf.p_lessThan(RealScalar.of(0.1)), RealScalar.ZERO);
    assertEquals(pdf.p_lessThan(RealScalar.of(1)), RationalScalar.of(0, 10));
    assertEquals(pdf.p_lessThan(RealScalar.of(1.1)), RationalScalar.of(9, 10));
    assertEquals(pdf.p_lessThan(RealScalar.of(2)), RationalScalar.of(9, 10));
    assertEquals(pdf.p_lessThan(RealScalar.of(3)), RationalScalar.of(10, 10));
  }

  public void testRandomVariate() {
    Distribution distribution = CategoricalDistribution.fromUnscaledPDF(Tensors.vector(0, 2, 1, 0, 3, 0));
    Map<Tensor, Long> map = Tally.of(RandomVariate.of(distribution, 100));
    assertFalse(map.containsKey(RealScalar.ZERO));
    assertTrue(map.containsKey(RealScalar.of(1)));
    assertTrue(map.containsKey(RealScalar.of(2)));
    assertFalse(map.containsKey(RealScalar.of(3)));
    assertTrue(map.containsKey(RealScalar.of(4)));
    assertFalse(map.containsKey(RealScalar.of(5)));
    assertEquals(distribution.toString(), "CategoricalDistribution[{0, 1/3, 1/6, 0, 1/2, 0}]");
  }

  public void testNextDown() {
    AbstractDiscreteDistribution distribution = //
        CategoricalDistribution.fromUnscaledPDF(Tensors.vector(Math.PI, 2., 1., 1.123123, 3., 0, 0, 0));
    Scalar s = distribution.quantile(RealScalar.of(Math.nextDown(1.0)));
    assertEquals(s, RealScalar.of(4));
  }

  public void testRandomVariateNeedle1() {
    AbstractDiscreteDistribution distribution = //
        CategoricalDistribution.fromUnscaledPDF(Tensors.vector(0, 2, 1, 0, 3, 0));
    assertEquals(distribution.quantile(RealScalar.of(0)), RealScalar.ONE);
    assertEquals(distribution.quantile(RealScalar.of(0.99999999999)), RealScalar.of(4));
  }

  public void testRandomVariateNeedle2() {
    AbstractDiscreteDistribution distribution = //
        CategoricalDistribution.fromUnscaledPDF(Tensors.vector(0, 0, 1, 0, 1, 0));
    assertEquals(distribution.quantile(RealScalar.of(0)), RealScalar.of(2));
    assertEquals(distribution.quantile(RealScalar.of(Math.nextDown(0.5))), RealScalar.of(2));
    assertEquals(distribution.quantile(RationalScalar.of(1, 2)), RealScalar.of(2));
    assertEquals(distribution.quantile(RealScalar.of(Math.nextDown(1.0))), RealScalar.of(4));
  }

  public void testVariance() {
    Distribution distribution = //
        CategoricalDistribution.fromUnscaledPDF(Tensors.vector(0, 0, 1, 0, 1, 0));
    Scalar var = Variance.of(distribution);
    Scalar mean = Mean.of(distribution);
    ExactScalarQ.require(mean);
    assertEquals(mean, RealScalar.of(3));
    ExactScalarQ.require(var);
    assertEquals(var, RealScalar.ONE);
  }

  public void testInverseCDF() {
    InverseCDF inverseCDF = InverseCDF.of(CategoricalDistribution.fromUnscaledPDF(Tensors.vector(0, 3, 1)));
    Scalar x0 = inverseCDF.quantile(RealScalar.ZERO);
    Scalar x1 = inverseCDF.quantile(RealScalar.of(0.5));
    Scalar x2 = inverseCDF.quantile(RealScalar.of(0.8));
    // Scalar x3 = inv.quantile(RealScalar.of(1)); // at the moment: forbidden
    assertEquals(x0, RealScalar.ONE);
    assertEquals(x0, x1);
    assertEquals(x2, RealScalar.of(2));
  }

  public void testInverseCDFOne() {
    AbstractDiscreteDistribution distribution = //
        CategoricalDistribution.fromUnscaledPDF(Tensors.vector(0, 0, 1, 0, 1, 0, 0, 0));
    assertEquals(distribution.quantile(RealScalar.of(1)), RealScalar.of(4));
  }

  public void testToString() {
    Distribution distribution = CategoricalDistribution.fromUnscaledPDF(Tensors.vector(0, 9, 1));
    assertTrue(distribution.toString().startsWith("CategoricalDistribution["));
  }

  public void testQuantity() {
    Distribution distribution = CategoricalDistribution.fromUnscaledPDF(Tensors.fromString("{1[m], 2[m]}"));
    assertEquals(PDF.of(distribution).at(RealScalar.of(0)), RationalScalar.of(1, 3));
    assertEquals(PDF.of(distribution).at(RealScalar.of(1)), RationalScalar.of(2, 3));
  }

  public void testFailInverseCDF() {
    InverseCDF inverseCDF = InverseCDF.of(CategoricalDistribution.fromUnscaledPDF(Tensors.vector(0, 3, 1)));
    AssertFail.of(() -> inverseCDF.quantile(RealScalar.of(-0.1)));
    AssertFail.of(() -> inverseCDF.quantile(RealScalar.of(1.1)));
  }

  public void testWrongReference() {
    AbstractDiscreteDistribution distribution = //
        CategoricalDistribution.fromUnscaledPDF(Tensors.vector(0, 0, 1, 0, 1, 0));
    AssertFail.of(() -> distribution.quantile(RealScalar.of(Math.nextDown(0.0))));
  }

  public void testNegativeFail() {
    AssertFail.of(() -> CategoricalDistribution.fromUnscaledPDF(Tensors.vector(0, -9, 1)));
  }

  public void testZeroFail() {
    AssertFail.of(() -> CategoricalDistribution.fromUnscaledPDF(Tensors.vector(0, 0, 0)));
  }

  public void testEmptyFail() {
    AssertFail.of(() -> CategoricalDistribution.fromUnscaledPDF(Tensors.empty()));
  }

  public void testScalarFail() {
    AssertFail.of(() -> CategoricalDistribution.fromUnscaledPDF(RealScalar.ONE));
  }

  public void testMatrixFail() {
    AssertFail.of(() -> CategoricalDistribution.fromUnscaledPDF(HilbertMatrix.of(10)));
  }
}
