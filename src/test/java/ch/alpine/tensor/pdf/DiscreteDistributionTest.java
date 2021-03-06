// code by jph
package ch.alpine.tensor.pdf;

import ch.alpine.tensor.DeterminateScalarQ;
import ch.alpine.tensor.IntegerQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.red.InterquartileRange;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Median;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class DiscreteDistributionTest extends TestCase {
  static final Distribution[] DISTRIBUTIONS = { //
      BernoulliDistribution.of(0.3), //
      BinomialDistribution.of(5, .4), //
      DiscreteUniformDistribution.of(2, 10), //
      EmpiricalDistribution.fromUnscaledPDF(Tensors.vector(1, 2, 0, 2, 3, 1)), //
      GeometricDistribution.of(.3), //
      HypergeometricDistribution.of(2, 2, 10), //
      NegativeBinomialDistribution.of(4, 0.8), //
      PascalDistribution.of(3, .2), //
      PoissonBinomialDistribution.of(Tensors.vector(.2, .4, .2, .2)), //
      PoissonDistribution.of(0.3), //
  };

  public void testInverseCDF() {
    for (Distribution distribution : DISTRIBUTIONS)
      if (distribution instanceof InverseCDF) {
        InverseCDF inverseCDF = InverseCDF.of(distribution);
        Scalar scalar = Median.of(distribution);
        DeterminateScalarQ.require(scalar);
        IntegerQ.require(scalar);
        AssertFail.of(() -> inverseCDF.quantile(RealScalar.of(-0.1)));
        AssertFail.of(() -> inverseCDF.quantile(RealScalar.of(+1.1)));
        DeterminateScalarQ.require(inverseCDF.quantile(RealScalar.ZERO));
        DeterminateScalarQ.require(inverseCDF.quantile(RealScalar.of(Math.nextDown(1))));
      }
  }

  public void testInverseCDFIncreasing() {
    for (Distribution distribution : DISTRIBUTIONS)
      if (distribution instanceof InverseCDF) {
        InverseCDF inverseCDF = InverseCDF.of(distribution);
        Sign.requirePositiveOrZero(InterquartileRange.of(distribution));
        Scalar lo = inverseCDF.quantile(RationalScalar.of(1, 8));
        Scalar hi = inverseCDF.quantile(RationalScalar.of(3, 8));
        assertTrue(Scalars.lessEquals(lo, hi));
      }
  }

  public void testMean() {
    for (Distribution distribution : DISTRIBUTIONS) {
      RandomVariate.of(distribution);
      Scalar scalar = Mean.of(distribution);
      DeterminateScalarQ.require(scalar);
    }
  }

  public void testVariance() {
    for (Distribution distribution : DISTRIBUTIONS) {
      Scalar scalar = Variance.of(distribution);
      DeterminateScalarQ.require(scalar);
      // System.out.println(distribution);
    }
  }
}
