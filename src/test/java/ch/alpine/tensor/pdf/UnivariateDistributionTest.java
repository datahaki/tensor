// code by jph
package ch.alpine.tensor.pdf;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.pdf.c.CauchyDistribution;
import ch.alpine.tensor.pdf.c.DagumDistribution;
import ch.alpine.tensor.pdf.c.EqualizingDistribution;
import ch.alpine.tensor.pdf.c.ExponentialDistribution;
import ch.alpine.tensor.pdf.c.FrechetDistribution;
import ch.alpine.tensor.pdf.c.GompertzMakehamDistribution;
import ch.alpine.tensor.pdf.c.GumbelDistribution;
import ch.alpine.tensor.pdf.c.HistogramDistribution;
import ch.alpine.tensor.pdf.c.LaplaceDistribution;
import ch.alpine.tensor.pdf.c.LogNormalDistribution;
import ch.alpine.tensor.pdf.c.LogisticDistribution;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.ParetoDistribution;
import ch.alpine.tensor.pdf.c.RayleighDistribution;
import ch.alpine.tensor.pdf.c.TrapezoidalDistribution;
import ch.alpine.tensor.red.InterquartileRange;
import ch.alpine.tensor.red.Median;
import ch.alpine.tensor.sca.Sign;

class UnivariateDistributionTest {
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

  @Test
  void testSimple() {
    for (Distribution distribution : DISTRIBUTIONS) {
      RandomVariate.of(distribution);
      InverseCDF inverseCDF = InverseCDF.of(distribution);
      Scalar scalar = Median.of(distribution);
      assertTrue(FiniteScalarQ.of(scalar));
      assertThrows(Throw.class, () -> inverseCDF.quantile(RealScalar.of(-0.1)));
      assertThrows(Exception.class, () -> inverseCDF.quantile(RealScalar.of(+1.1)));
      inverseCDF.quantile(RealScalar.ZERO);
      inverseCDF.quantile(RealScalar.of(Math.nextUp(0)));
      assertTrue(FiniteScalarQ.of(inverseCDF.quantile(RealScalar.of(Math.nextDown(1)))));
    }
  }

  @Test
  void testInverseCDFIncreasing() {
    for (Distribution distribution : DISTRIBUTIONS) {
      InverseCDF inverseCDF = InverseCDF.of(distribution);
      Sign.requirePositiveOrZero(InterquartileRange.of(distribution));
      Scalar lo = inverseCDF.quantile(RationalScalar.of(1, 8));
      Scalar hi = inverseCDF.quantile(RationalScalar.of(3, 8));
      assertTrue(Scalars.lessEquals(lo, hi));
    }
  }
}
