// code by jph
package ch.ethz.idsc.tensor.pdf;

import ch.ethz.idsc.tensor.DeterminateScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.InterquartileRange;
import ch.ethz.idsc.tensor.red.Median;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ContinuousDistributionTest extends TestCase {
  static final Distribution[] DISTRIBUTIONS = { //
      CauchyDistribution.of(.2, .3), //
      DagumDistribution.of(.3, .4, .5), //
      EqualizingDistribution.fromUnscaledPDF(Tensors.vector(1, 2, 0, .2, 4)), //
      ExponentialDistribution.of(.3), //
      FrechetDistribution.of(.3, .4), //
      GompertzMakehamDistribution.of(.3, .4), //
      GumbelDistribution.of(.2, .3), //
      HistogramDistribution.of(Tensors.vector(3, 2, 1, 5, 3, 2)), //
      LaplaceDistribution.of(.3, .4), //
      LogisticDistribution.of(.2, .3), //
      LogNormalDistribution.of(.2, .3), //
      NormalDistribution.of(2.3, 3.1), //
      ParetoDistribution.of(.2, .3), //
      RayleighDistribution.of(.3), //
      TrapezoidalDistribution.of(-2, 1, 3, 6), //
  };

  public void testSimple() {
    for (Distribution distribution : DISTRIBUTIONS) {
      RandomVariate.of(distribution);
      InverseCDF inverseCDF = InverseCDF.of(distribution);
      Scalar scalar = Median.of(distribution);
      DeterminateScalarQ.require(scalar);
      AssertFail.of(() -> inverseCDF.quantile(RealScalar.of(-0.1)));
      AssertFail.of(() -> inverseCDF.quantile(RealScalar.of(+1.1)));
      inverseCDF.quantile(RealScalar.ZERO);
      inverseCDF.quantile(RealScalar.of(Math.nextUp(0)));
      DeterminateScalarQ.require(inverseCDF.quantile(RealScalar.of(Math.nextDown(1))));
    }
  }

  public void testInverseCDFIncreasing() {
    for (Distribution distribution : DISTRIBUTIONS) {
      InverseCDF inverseCDF = InverseCDF.of(distribution);
      Sign.requirePositiveOrZero(InterquartileRange.of(distribution));
      Scalar lo = inverseCDF.quantile(RationalScalar.of(1, 8));
      Scalar hi = inverseCDF.quantile(RationalScalar.of(3, 8));
      assertTrue(Scalars.lessEquals(lo, hi));
    }
  }
}
