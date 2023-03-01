// code by jph
package ch.alpine.tensor.pdf;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.chq.IntegerQ;
import ch.alpine.tensor.pdf.d.BernoulliDistribution;
import ch.alpine.tensor.pdf.d.BinomialDistribution;
import ch.alpine.tensor.pdf.d.CategoricalDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.pdf.d.GeometricDistribution;
import ch.alpine.tensor.pdf.d.HypergeometricDistribution;
import ch.alpine.tensor.pdf.d.NegativeBinomialDistribution;
import ch.alpine.tensor.pdf.d.PascalDistribution;
import ch.alpine.tensor.pdf.d.PoissonBinomialDistribution;
import ch.alpine.tensor.pdf.d.PoissonDistribution;
import ch.alpine.tensor.red.InterquartileRange;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Median;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Sign;

class DiscreteDistributionTest {
  private static final Distribution[] DISTRIBUTIONS = { //
      BernoulliDistribution.of(0.3), //
      BinomialDistribution.of(5, .4), //
      DiscreteUniformDistribution.of(2, 10), //
      CategoricalDistribution.fromUnscaledPDF(Tensors.vector(1, 2, 0, 2, 3, 1)), //
      GeometricDistribution.of(.3), //
      HypergeometricDistribution.of(2, 2, 10), //
      NegativeBinomialDistribution.of(4, 0.8), //
      PascalDistribution.of(3, .2), //
      PoissonBinomialDistribution.of(Tensors.vector(.2, .4, .2, .2)), //
      PoissonDistribution.of(0.3), //
  };

  private static Distribution[] distributions() {
    return DISTRIBUTIONS;
  }

  @MethodSource("distributions")
  @ParameterizedTest
  void testInverseCDF(Distribution distribution) {
    if (distribution instanceof InverseCDF) {
      InverseCDF inverseCDF = InverseCDF.of(distribution);
      Scalar scalar = Median.of(distribution);
      assertTrue(FiniteScalarQ.of(scalar));
      IntegerQ.require(scalar);
      assertThrows(Throw.class, () -> inverseCDF.quantile(RealScalar.of(-0.1)));
      assertThrows(Exception.class, () -> inverseCDF.quantile(RealScalar.of(+1.1)));
      assertTrue(FiniteScalarQ.of(inverseCDF.quantile(RealScalar.ZERO)));
      assertTrue(FiniteScalarQ.of(inverseCDF.quantile(RealScalar.of(Math.nextDown(1)))));
    }
  }

  @MethodSource("distributions")
  @ParameterizedTest
  void testInverseCDFIncreasing(Distribution distribution) {
    if (distribution instanceof InverseCDF) {
      InverseCDF inverseCDF = InverseCDF.of(distribution);
      Sign.requirePositiveOrZero(InterquartileRange.of(distribution));
      Scalar lo = inverseCDF.quantile(RationalScalar.of(1, 8));
      Scalar hi = inverseCDF.quantile(RationalScalar.of(3, 8));
      assertTrue(Scalars.lessEquals(lo, hi));
    }
  }

  @MethodSource("distributions")
  @ParameterizedTest
  void testMean(Distribution distribution) {
    RandomVariate.of(distribution);
    Scalar scalar = Mean.of(distribution);
    assertTrue(FiniteScalarQ.of(scalar));
  }

  @MethodSource("distributions")
  @ParameterizedTest
  void testVariance(Distribution distribution) {
    Scalar scalar = Variance.of(distribution);
    assertTrue(FiniteScalarQ.of(scalar));
  }
}
