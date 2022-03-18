// code by jph
package ch.alpine.tensor.pdf.d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.red.CentralMoment;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Tally;
import ch.alpine.tensor.red.Variance;

public class CategoricalDistributionTest {
  @Test
  public void testPDF() {
    Distribution distribution = CategoricalDistribution.fromUnscaledPDF(Tensors.vector(0, 9, 1));
    PDF pdf = PDF.of(distribution);
    assertEquals(pdf.at(RealScalar.of(0)), RealScalar.ZERO);
    assertEquals(pdf.at(RealScalar.of(1)), RationalScalar.of(9, 10));
    assertEquals(pdf.at(RealScalar.of(2)), RationalScalar.of(1, 10));
  }

  @Test
  public void testP_Equals() {
    CategoricalDistribution categoricalDistribution = CategoricalDistribution.fromUnscaledPDF(Tensors.vector(0, 9, 1, 5));
    assertEquals(categoricalDistribution.p_equals(0), RationalScalar.of(0, 1));
    assertEquals(categoricalDistribution.p_equals(1), RationalScalar.of(9, 15));
    assertEquals(categoricalDistribution.p_equals(2), RationalScalar.of(1, 15));
    assertEquals(categoricalDistribution.p_equals(3), RationalScalar.of(5, 15));
  }

  @Test
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

  @Test
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

  @Test
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
    assertEquals(Variance.of(distribution), CentralMoment.of(distribution, 2));
  }

  @Test
  public void testNextDown() {
    AbstractDiscreteDistribution distribution = //
        CategoricalDistribution.fromUnscaledPDF(Tensors.vector(Math.PI, 2., 1., 1.123123, 3., 0, 0, 0));
    Scalar s = distribution.quantile(RealScalar.of(Math.nextDown(1.0)));
    assertEquals(s, RealScalar.of(4));
  }

  @Test
  public void testRandomVariateNeedle1() {
    AbstractDiscreteDistribution distribution = //
        CategoricalDistribution.fromUnscaledPDF(Tensors.vector(0, 2, 1, 0, 3, 0));
    assertEquals(distribution.quantile(RealScalar.of(0)), RealScalar.ONE);
    assertEquals(distribution.quantile(RealScalar.of(0.99999999999)), RealScalar.of(4));
  }

  @Test
  public void testRandomVariateNeedle2() {
    AbstractDiscreteDistribution distribution = //
        CategoricalDistribution.fromUnscaledPDF(Tensors.vector(0, 0, 1, 0, 1, 0));
    assertEquals(distribution.quantile(RealScalar.of(0)), RealScalar.of(2));
    assertEquals(distribution.quantile(RealScalar.of(Math.nextDown(0.5))), RealScalar.of(2));
    assertEquals(distribution.quantile(RationalScalar.of(1, 2)), RealScalar.of(2));
    assertEquals(distribution.quantile(RealScalar.of(Math.nextDown(1.0))), RealScalar.of(4));
  }

  @Test
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

  @Test
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

  @Test
  public void testInverseCDFOne() {
    AbstractDiscreteDistribution distribution = //
        CategoricalDistribution.fromUnscaledPDF(Tensors.vector(0, 0, 1, 0, 1, 0, 0, 0));
    assertEquals(distribution.quantile(RealScalar.of(1)), RealScalar.of(4));
    Scalar variance = Variance.of(distribution);
    ExactScalarQ.require(variance);
    assertEquals(variance, RealScalar.ONE);
  }

  @Test
  public void testToString() {
    Distribution distribution = CategoricalDistribution.fromUnscaledPDF(Tensors.vector(0, 9, 1));
    assertTrue(distribution.toString().startsWith("CategoricalDistribution["));
  }

  @Test
  public void testQuantity() {
    Distribution distribution = CategoricalDistribution.fromUnscaledPDF(Tensors.fromString("{1[m], 2[m]}"));
    assertEquals(PDF.of(distribution).at(RealScalar.of(0)), RationalScalar.of(1, 3));
    assertEquals(PDF.of(distribution).at(RealScalar.of(1)), RationalScalar.of(2, 3));
  }

  @Test
  public void testMarkov() {
    Distribution distribution = CategoricalDistribution.fromUnscaledPDF(Tensors.vector(1, 2, 3, 2, 4, 0, 2));
    TestMarkovChebyshev.markov(distribution);
    TestMarkovChebyshev.chebyshev(distribution);
  }

  @Test
  public void testFailInverseCDF() {
    InverseCDF inverseCDF = InverseCDF.of(CategoricalDistribution.fromUnscaledPDF(Tensors.vector(0, 3, 1)));
    assertThrows(TensorRuntimeException.class, () -> inverseCDF.quantile(RealScalar.of(-0.1)));
    assertThrows(NullPointerException.class, () -> inverseCDF.quantile(RealScalar.of(1.1)));
  }

  @Test
  public void testWrongReference() {
    AbstractDiscreteDistribution distribution = //
        CategoricalDistribution.fromUnscaledPDF(Tensors.vector(0, 0, 1, 0, 1, 0));
    assertThrows(TensorRuntimeException.class, () -> distribution.quantile(RealScalar.of(Math.nextDown(0.0))));
  }

  @Test
  public void testNegativeFail() {
    assertThrows(TensorRuntimeException.class, () -> CategoricalDistribution.fromUnscaledPDF(Tensors.vector(0, -9, 1)));
  }

  @Test
  public void testZeroFail() {
    assertThrows(ArithmeticException.class, () -> CategoricalDistribution.fromUnscaledPDF(Tensors.vector(0, 0, 0)));
  }

  @Test
  public void testEmptyFail() {
    assertThrows(IndexOutOfBoundsException.class, () -> CategoricalDistribution.fromUnscaledPDF(Tensors.empty()));
  }

  @Test
  public void testScalarFail() {
    assertThrows(TensorRuntimeException.class, () -> CategoricalDistribution.fromUnscaledPDF(RealScalar.ONE));
  }

  @Test
  public void testMatrixFail() {
    assertThrows(ClassCastException.class, () -> CategoricalDistribution.fromUnscaledPDF(HilbertMatrix.of(10)));
  }
}
