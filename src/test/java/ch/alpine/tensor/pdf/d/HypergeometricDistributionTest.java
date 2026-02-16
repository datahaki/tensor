// code by jph
package ch.alpine.tensor.pdf.d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;

class HypergeometricDistributionTest {
  @Test
  void testPdf() {
    PDF pdf = PDF.of(HypergeometricDistribution.of(10, 50, 100));
    Scalar sum = RealScalar.ZERO;
    for (int c = 0; c <= 10; ++c)
      sum = sum.add(pdf.at(RealScalar.of(c)));
    assertEquals(sum, RealScalar.ONE);
    assertEquals(pdf.at(RealScalar.of(100000)), RealScalar.ZERO);
  }

  @Test
  void testFail() {
    // int N, int n, int m_n
    // 0 < N && N <= m_n && 0 <= n && n <= m_n
    assertThrows(IllegalArgumentException.class, () -> HypergeometricDistribution.of(0, 50, 100)); // violates 0 < N
    assertThrows(IllegalArgumentException.class, () -> HypergeometricDistribution.of(5, -1, 100)); // violates 0 <= n
    assertThrows(IllegalArgumentException.class, () -> HypergeometricDistribution.of(11, 10, 10)); // violates N <= m_n
    assertThrows(IllegalArgumentException.class, () -> HypergeometricDistribution.of(10, 11, 10)); // violates n <= m_n
  }

  @Test
  void testSpecialCase() {
    PDF pdf = PDF.of(HypergeometricDistribution.of(10, 0, 100));
    assertEquals(pdf.at(RealScalar.of(-1)), RealScalar.ZERO);
    assertEquals(pdf.at(RealScalar.of(0)), RealScalar.ONE);
    assertEquals(pdf.at(RealScalar.of(1)), RealScalar.ZERO);
    assertEquals(pdf.at(RealScalar.of(10)), RealScalar.ZERO);
  }

  @Test
  void testInverseCDF1() {
    Distribution distribution = HypergeometricDistribution.of(10, 0, 100);
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    Scalar r = inverseCDF.quantile(RealScalar.ONE);
    assertEquals(r, RealScalar.ZERO);
  }

  @Test
  void testInverseCDF2() {
    Distribution distribution = HypergeometricDistribution.of(10, 5, 100);
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    assertEquals(inverseCDF.quantile(RealScalar.ONE), RealScalar.of(5)); // confirmed with Mathematica
  }

  @Test
  void testInverseCDF3() {
    Distribution distribution = HypergeometricDistribution.of(6, 10, 100);
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    assertEquals(inverseCDF.quantile(RealScalar.ONE), RealScalar.of(6)); // confirmed with Mathematica
  }

  @Test
  void testOutside() {
    PDF pdf = PDF.of(HypergeometricDistribution.of(10, 50, 100));
    assertEquals(pdf.at(RealScalar.of(-1)), RealScalar.ZERO);
    assertEquals(pdf.at(RealScalar.of(11)), RealScalar.ZERO);
  }

  @Test
  void testMean() {
    Scalar mean = Mean.of(HypergeometricDistribution.of(10, 50, 100));
    assertEquals(mean, RealScalar.of(5));
  }

  @Test
  void testVariance() {
    Scalar variance = Variance.of(HypergeometricDistribution.of(10, 50, 100));
    assertEquals(variance, Scalars.fromString("25/11"));
  }

  @Test
  void testToString() {
    Distribution distribution = HypergeometricDistribution.of(10, 50, 100);
    assertEquals(distribution.toString(), "HypergeometricDistribution[10, 50, 100]");
  }

  @Test
  void testCDFMathematica() {
    int n = 5;
    Distribution distribution = HypergeometricDistribution.of(10, 50, 100);
    CDF cdf = CDF.of(distribution);
    Tensor actual = Range.of(0, n + 1).maps(cdf::p_lessEquals);
    Tensor expect = Tensors.fromString( //
        "{1763/2970916, 23263/2970916, 68069/1485458, 236069/1485458, 6051259/16340038, 10288779/16340038}");
    assertEquals(actual, expect);
  }

  @Test
  void testInverseCDFMathematica() {
    Distribution distribution = HypergeometricDistribution.of(10, 50, 100);
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    Scalar actual = inverseCDF.quantile(Rational.of(23263, 2970916));
    Scalar expect = RealScalar.ONE;
    assertEquals(actual, expect);
  }

  @Test
  void testCDFInverseCDF() {
    int n = 10;
    Distribution distribution = HypergeometricDistribution.of(10, 50, 100);
    CDF cdf = CDF.of(distribution);
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    for (Tensor _x : Range.of(0, n + 1)) {
      Scalar x = (Scalar) _x;
      Scalar q = inverseCDF.quantile(cdf.p_lessEquals(x));
      assertEquals(x, q);
    }
  }

  @Test
  void testMonotonous() {
    TestMarkovChebyshev.monotonous(HypergeometricDistribution.of(10, 50, 100));
  }
}
