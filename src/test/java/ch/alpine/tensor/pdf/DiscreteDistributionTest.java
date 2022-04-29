// code by jph
package ch.alpine.tensor.pdf;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.IntegerQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.FiniteScalarQ;
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

public class DiscreteDistributionTest {
  static final Distribution[] DISTRIBUTIONS = { //
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

  @Test
  public void testInverseCDF() {
    for (Distribution distribution : DISTRIBUTIONS)
      if (distribution instanceof InverseCDF) {
        InverseCDF inverseCDF = InverseCDF.of(distribution);
        Scalar scalar = Median.of(distribution);
        FiniteScalarQ.require(scalar);
        IntegerQ.require(scalar);
        assertThrows(TensorRuntimeException.class, () -> inverseCDF.quantile(RealScalar.of(-0.1)));
        assertThrows(Exception.class, () -> inverseCDF.quantile(RealScalar.of(+1.1)));
        FiniteScalarQ.require(inverseCDF.quantile(RealScalar.ZERO));
        FiniteScalarQ.require(inverseCDF.quantile(RealScalar.of(Math.nextDown(1))));
      }
  }

  @Test
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

  @Test
  public void testMean() {
    for (Distribution distribution : DISTRIBUTIONS) {
      RandomVariate.of(distribution);
      Scalar scalar = Mean.of(distribution);
      FiniteScalarQ.require(scalar);
    }
  }

  @Test
  public void testVariance() {
    for (Distribution distribution : DISTRIBUTIONS) {
      Scalar scalar = Variance.of(distribution);
      FiniteScalarQ.require(scalar);
      // System.out.println(distribution);
    }
  }
}
