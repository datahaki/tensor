// code by jph
package ch.alpine.tensor.pdf.d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.red.Tally;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.N;

class BernoulliDistributionTest {
  @Test
  void testEquals() {
    Scalar p = RationalScalar.of(1, 3);
    Distribution distribution = BernoulliDistribution.of(p);
    PDF pdf = PDF.of(distribution);
    // PDF[BernoulliDistribution[1/3], 0] == 2/3
    assertEquals(pdf.at(RealScalar.of(0)), RationalScalar.of(2, 3));
    assertEquals(pdf.at(RealScalar.of(1)), RationalScalar.of(1, 3));
    assertEquals(pdf.at(RealScalar.of(2)), RealScalar.ZERO);
  }

  @Test
  void testLessThan() {
    Scalar p = RationalScalar.of(1, 3);
    Distribution distribution = BernoulliDistribution.of(p);
    CDF cdf = CDF.of(distribution);
    assertEquals(cdf.p_lessThan(RealScalar.of(0)), RealScalar.ZERO);
    assertEquals(cdf.p_lessThan(RealScalar.of(1)), RationalScalar.of(2, 3));
    assertEquals(cdf.p_lessThan(RealScalar.of(2)), RealScalar.ONE);
  }

  @Test
  void testLessEquals() {
    Scalar p = RationalScalar.of(1, 3);
    Distribution distribution = BernoulliDistribution.of(p);
    CDF cdf = CDF.of(distribution);
    assertEquals(cdf.p_lessEquals(RealScalar.of(0)), RationalScalar.of(2, 3));
    assertEquals(cdf.p_lessEquals(RealScalar.of(1)), RealScalar.ONE);
    assertEquals(cdf.p_lessEquals(RealScalar.of(2)), RealScalar.ONE);
  }

  @Test
  void testSample() {
    final Scalar p = RationalScalar.of(1, 3);
    Distribution distribution = BernoulliDistribution.of(p);
    Tensor list = RandomVariate.of(distribution, 2000);
    Map<Tensor, Long> map = Tally.of(list);
    long v0 = map.get(RealScalar.ZERO);
    long v1 = map.get(RealScalar.ONE);
    Scalar ratio = RationalScalar.of(v1, v0 + v1);
    Scalar dev = N.DOUBLE.of(Abs.between(ratio, p));
    assertTrue(Scalars.lessThan(dev, RealScalar.of(0.07)));
  }

  @Test
  void testMarkov() {
    TestMarkovChebyshev.markov(BernoulliDistribution.of(0));
    TestMarkovChebyshev.markov(BernoulliDistribution.of(1));
    TestMarkovChebyshev.markov(BernoulliDistribution.of(0.5));
    TestMarkovChebyshev.markov(BernoulliDistribution.of(0.3));
    TestMarkovChebyshev.chebyshev(BernoulliDistribution.of(0.0));
  }

  @Test
  void testNumber() {
    Distribution distribution = BernoulliDistribution.of(0.5);
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    assertEquals(inverseCDF.quantile(RealScalar.of(0.50)), RealScalar.ZERO);
    assertEquals(inverseCDF.quantile(RealScalar.of(0.51)), RealScalar.ONE);
    assertThrows(Throw.class, () -> inverseCDF.quantile(RealScalar.of(-0.1)));
    assertThrows(NullPointerException.class, () -> inverseCDF.quantile(RealScalar.of(1.1)));
  }

  @Test
  void testMonotonous() {
    TestMarkovChebyshev.monotonous(BernoulliDistribution.of(0.2));
  }

  @Test
  void testFailP() {
    assertThrows(Throw.class, () -> BernoulliDistribution.of(RationalScalar.of(-1, 3)));
    assertThrows(Throw.class, () -> BernoulliDistribution.of(RationalScalar.of(4, 3)));
  }

  @Test
  void testFailPNumber() {
    assertThrows(Throw.class, () -> BernoulliDistribution.of(-1e-10));
    assertThrows(Throw.class, () -> BernoulliDistribution.of(1.0001));
  }
}
