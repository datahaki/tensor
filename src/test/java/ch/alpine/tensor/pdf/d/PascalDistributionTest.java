// code by jph
package ch.alpine.tensor.pdf.d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.pow.Power;

public class PascalDistributionTest {
  @Test
  public void testPDF() {
    Scalar p = RationalScalar.of(2, 3);
    PascalDistribution distribution = (PascalDistribution) PascalDistribution.of(5, p);
    PDF pdf = PDF.of(distribution);
    Scalar scalar = pdf.at(RealScalar.of(5));
    assertEquals(scalar, Power.of(p, 5));
    assertTrue(Scalars.lessEquals(RealScalar.of(43), distribution.inverse_cdf().lastEntry().getValue()));
  }

  @Test
  public void testCDF() {
    Scalar p = RationalScalar.of(2, 3);
    Distribution distribution = PascalDistribution.of(5, p);
    CDF pdf = CDF.of(distribution);
    Scalar probability = pdf.p_lessEquals(RealScalar.of(14));
    assertEquals(probability, RationalScalar.of(4763648, 4782969));
    ExactScalarQ.require(probability);
  }

  @Test
  public void testMean() {
    Distribution distribution = PascalDistribution.of(5, RationalScalar.of(2, 3));
    assertTrue(distribution.toString().startsWith("PascalDistribution["));
    Scalar mean = Mean.of(distribution);
    Scalar var = Variance.of(distribution);
    assertEquals(mean, RationalScalar.of(15, 2));
    assertEquals(var, RationalScalar.of(15, 4));
    ExactScalarQ.require(mean);
    ExactScalarQ.require(var);
  }

  @Test
  public void testVariance() {
    PascalDistribution distribution = (PascalDistribution) PascalDistribution.of(11, RationalScalar.of(5, 17));
    Scalar mean = Mean.of(distribution);
    Scalar var = Variance.of(distribution);
    assertEquals(mean, RationalScalar.of(187, 5));
    assertEquals(var, RationalScalar.of(2244, 25));
    ExactScalarQ.require(mean);
    ExactScalarQ.require(var);
    assertTrue(Scalars.lessEquals(RealScalar.of(172), distribution.inverse_cdf().lastEntry().getValue()));
  }

  @Test
  public void testRandomVariate() {
    Scalar p = RationalScalar.of(3, 4);
    Distribution distribution = PascalDistribution.of(5, p);
    Tensor tensor = RandomVariate.of(distribution, 2300);
    Tensor mean = Mean.of(tensor);
    Scalar diff = Mean.of(distribution).subtract(mean);
    assertTrue(Scalars.lessThan(Abs.of(diff), RealScalar.of(0.2)));
    ExactScalarQ.require(diff);
  }

  @Test
  public void testInverseCdf() {
    Scalar p = RationalScalar.of(1, 5);
    PascalDistribution distribution = (PascalDistribution) PascalDistribution.of(5, p);
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    Scalar quantile = inverseCDF.quantile(RealScalar.of(0.999));
    assertTrue(Scalars.lessThan(quantile, distribution.inverse_cdf().lastEntry().getValue()));
    assertTrue(Scalars.isZero(distribution.p_equals(3)));
    assertTrue(Scalars.isZero(distribution.p_equals(4)));
    assertTrue(Scalars.nonZero(distribution.p_equals(5)));
  }

  @Test
  public void testCDFMathematica() {
    int n = 5;
    PascalDistribution distribution = (PascalDistribution) PascalDistribution.of(n, RationalScalar.of(1, 4));
    CDF cdf = CDF.of(distribution);
    Tensor actual = Range.of(0, 10 + 1).map(cdf::p_lessEquals);
    Tensor expect = Tensors.fromString("{0, 0, 0, 0, 0, 1/1024, 19/4096, 211/16384, 1789/65536, 6413/131072, 40961/524288}");
    assertEquals(actual, expect);
  }

  @Test
  public void testInverseCDFMathematica() {
    int n = 5;
    PascalDistribution distribution = (PascalDistribution) PascalDistribution.of(n, RationalScalar.of(1, 4));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    Scalar actual = inverseCDF.quantile(RationalScalar.of(19, 4096));
    Scalar expect = RealScalar.of(6);
    assertEquals(actual, expect);
  }

  @Test
  public void testCDFInverseCDF() {
    int n = 5;
    PascalDistribution distribution = (PascalDistribution) PascalDistribution.of(n, RationalScalar.of(1, 4));
    CDF cdf = CDF.of(distribution);
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    for (Tensor _x : Range.of(n, n + 30)) {
      Scalar x = (Scalar) _x;
      Scalar p = cdf.p_lessEquals(x);
      Scalar q = inverseCDF.quantile(p);
      assertEquals(x, q);
    }
  }

  @Test
  public void testFailN() {
    assertThrows(IllegalArgumentException.class, () -> PascalDistribution.of(0, RealScalar.of(0.2)));
    assertThrows(IllegalArgumentException.class, () -> PascalDistribution.of(-3, RealScalar.of(0.2)));
  }

  @Test
  public void testFailP() {
    assertThrows(TensorRuntimeException.class, () -> PascalDistribution.of(3, RealScalar.of(-0.2)));
    assertThrows(TensorRuntimeException.class, () -> PascalDistribution.of(3, RealScalar.of(1.2)));
  }
}
