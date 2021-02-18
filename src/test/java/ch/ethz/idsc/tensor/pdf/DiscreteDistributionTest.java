// code by jph
package ch.ethz.idsc.tensor.pdf;

import ch.ethz.idsc.tensor.DeterminateScalarQ;
import ch.ethz.idsc.tensor.IntegerQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.InterquartileRange;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Median;
import ch.ethz.idsc.tensor.red.Variance;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.usr.AssertFail;
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
