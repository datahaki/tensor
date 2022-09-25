// code by jph
package ch.alpine.tensor.pdf.d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.CentralMoment;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Quantile;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.ply.Polynomial;

class PoissonDistributionTest {
  static Tensor values(PDF pdf, int length) {
    return Tensors.vector(i -> pdf.at(RealScalar.of(i)), length);
  }

  @Test
  void testSingle() {
    Scalar lambda = RealScalar.of(2);
    Distribution distribution = PoissonDistribution.of(lambda);
    PDF pdf = PDF.of(distribution);
    assertTrue(pdf.at(RealScalar.ZERO).toString().startsWith("0.13533"));
    assertTrue(pdf.at(RealScalar.ONE).toString().startsWith("0.27067"));
    assertTrue(pdf.at(RealScalar.of(2)).toString().startsWith("0.27067"));
    assertTrue(pdf.at(RealScalar.of(3)).toString().startsWith("0.18044"));
    Tolerance.CHOP.requireClose(Variance.of(distribution), CentralMoment.of(distribution, 2));
    Tolerance.CHOP.requireClose(CentralMoment.of(distribution, 3), lambda);
    Chop._10.requireClose(CentralMoment.of(distribution, 4), Polynomial.of(Tensors.vector(0, 1, 3)).apply(lambda));
    Chop._08.requireClose(CentralMoment.of(distribution, 5), Polynomial.of(Tensors.vector(0, 1, 10)).apply(lambda));
  }

  @Test
  void testConvergence() {
    Distribution distribution = PoissonDistribution.of(RealScalar.of(2));
    PDF pdf = PDF.of(distribution);
    Tensor prob = values(pdf, 16);
    Scalar scalar = Total.ofVector(prob);
    assertTrue(Scalars.lessThan(RealScalar.of(0.9999), scalar));
    assertTrue(Scalars.lessThan(scalar, RealScalar.ONE));
  }

  @Test
  void testValues() {
    Distribution distribution = PoissonDistribution.of(RealScalar.of(3));
    PDF pdf = PDF.of(distribution);
    pdf.at(RealScalar.of(30));
    Tensor prob = values(pdf, 30);
    Scalar sum = Total.ofVector(prob);
    assertEquals(sum, RealScalar.ONE);
  }

  @Test
  void testPDF() {
    Distribution distribution = PoissonDistribution.of(RealScalar.of(10.5));
    CDF cdf = CDF.of(distribution);
    Scalar scalar = cdf.p_lessThan(RealScalar.of(50));
    assertEquals(Chop._12.of(scalar.subtract(RealScalar.ONE)), RealScalar.ZERO);
  }

  @Test
  void testPDF2() {
    Distribution distribution = PoissonDistribution.of(RealScalar.of(1.5));
    CDF cdf = CDF.of(distribution);
    Scalar scalar = cdf.p_lessThan(RealScalar.of(50));
    assertEquals(Chop._12.of(scalar.subtract(RealScalar.ONE)), RealScalar.ZERO);
  }

  @Test
  void testInverseCDF() {
    InverseCDF inverseCDF = InverseCDF.of(PoissonDistribution.of(RealScalar.of(5.5)));
    Scalar x0 = inverseCDF.quantile(RealScalar.of(0.0));
    Scalar x1 = inverseCDF.quantile(RealScalar.of(0.1));
    Scalar x2 = inverseCDF.quantile(RealScalar.of(0.5));
    assertEquals(x0, RealScalar.ZERO);
    assertTrue(Scalars.lessThan(x1, x2));
  }

  @Test
  void testCDFMathematica() {
    int n = 5;
    Distribution distribution = PoissonDistribution.of(RationalScalar.of(1, 4));
    CDF cdf = CDF.of(distribution);
    Tensor actual = Range.of(0, n + 1).map(cdf::p_lessEquals);
    Tensor expect = Tensors
        .fromString("{0.7788007830714049`, 0.9735009788392561`, 0.9978385033102375`, 0.999866630349486`, 0.999993388289439`, 0.9999997261864366`}");
    Tolerance.CHOP.requireClose(actual, expect);
  }

  @Test
  void testInverseCDFMathematica() {
    Distribution distribution = PoissonDistribution.of(RationalScalar.of(1, 4));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    Scalar actual = inverseCDF.quantile(RealScalar.of(0.9735009788392561));
    Scalar expect = RealScalar.ONE;
    assertEquals(actual, expect);
  }

  @Test
  void testMarkov() {
    Random random = new Random();
    Distribution distribution = PoissonDistribution.of(0.1 + 10 * random.nextDouble());
    TestMarkovChebyshev.markov(distribution);
    TestMarkovChebyshev.chebyshev(distribution);
  }

  @Test
  void testToString() {
    Distribution distribution = PoissonDistribution.of(RealScalar.of(5.5));
    String string = distribution.toString();
    assertEquals(string, "PoissonDistribution[5.5]");
  }

  @Test
  void testQuantityFail() {
    assertThrows(Throw.class, () -> PoissonDistribution.of(Quantity.of(3, "m")));
  }

  @Test
  void testFailLambda() {
    assertThrows(Throw.class, () -> PoissonDistribution.of(RealScalar.ZERO));
    assertThrows(Throw.class, () -> PoissonDistribution.of(RealScalar.of(-0.1)));
  }

  @Test
  void testLarge() {
    Distribution distribution = PoissonDistribution.of(RealScalar.of(700));
    PDF pdf = PDF.of(distribution);
    assertTrue(Scalars.isZero(pdf.at(RealScalar.of(140.123))));
    assertTrue(Scalars.nonZero(pdf.at(RealScalar.of(1942))));
    assertTrue(Scalars.isZero(pdf.at(RealScalar.of(1945))));
    assertTrue(Scalars.isZero(pdf.at(RealScalar.of(10000000))));
    assertTrue(Scalars.isZero(pdf.at(RealScalar.of(-1))));
    assertTrue(Scalars.isZero(pdf.at(RealScalar.of(-10000000))));
    assertTrue(Scalars.isZero(pdf.at(RealScalar.of(-1000000.12))));
  }

  @Test
  void testNextDownOne() {
    Scalar last = RealScalar.of(Math.nextDown(1.0));
    for (int lambda = 1; lambda < 700; lambda += 30) {
      Distribution distribution = PoissonDistribution.of(lambda);
      Scalar scalar = Quantile.of(distribution).apply(last);
      ExactScalarQ.require(scalar);
      assertTrue(Scalars.lessEquals(Mean.of(distribution), scalar));
    }
  }

  private static void _checkDiscreteCDFNumerics(Distribution distribution) {
    CDF cdf = CDF.of(distribution);
    // DiscreteCDF discreteCDF = (DiscreteCDF) cdf;
    assertEquals(cdf.p_lessEquals(RealScalar.of(-10)), RealScalar.ZERO);
    // assertFalse(discreteCDF.cdf_finished());
    Scalar top = cdf.p_lessEquals(RealScalar.of(1000000));
    Chop._14.requireClose(top, RealScalar.ONE);
    // assertTrue(discreteCDF.cdf_finished());
  }

  @Test
  void testNumericsPoisson() {
    _checkDiscreteCDFNumerics(PoissonDistribution.of(0.1));
    _checkDiscreteCDFNumerics(PoissonDistribution.of(1.0));
    _checkDiscreteCDFNumerics(PoissonDistribution.of(70));
    _checkDiscreteCDFNumerics(PoissonDistribution.of(700.0));
  }

  @Test
  void testMonotonous() {
    TestMarkovChebyshev.monotonous(PoissonDistribution.of(3.4));
  }

  @Test
  void testFailPoisson() {
    assertThrows(Throw.class, () -> PoissonDistribution.of(RealScalar.of(800)));
  }
}
