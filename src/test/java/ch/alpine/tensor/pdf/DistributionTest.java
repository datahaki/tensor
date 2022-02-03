// code by jph
package ch.alpine.tensor.pdf;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.pdf.c.ExponentialDistribution;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.pdf.d.BernoulliDistribution;
import ch.alpine.tensor.pdf.d.BinomialDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.pdf.d.GeometricDistribution;
import ch.alpine.tensor.pdf.d.HypergeometricDistribution;
import ch.alpine.tensor.pdf.d.PoissonDistribution;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Sqrt;
import junit.framework.TestCase;

public class DistributionTest extends TestCase {
  private static void _check(Distribution distribution, int n) {
    Tensor collect = RandomVariate.of(distribution, n);
    Scalar mean = (Scalar) Mean.of(collect); // measured mean
    Scalar var = Variance.ofVector(collect); // measured variance
    assertTrue(Scalars.nonZero(var));
    Scalar tmean = Expectation.mean(distribution); // theoretical mean
    Scalar tvar = Expectation.variance(distribution); // theoretical variance
    Scalar dmean = Abs.of(mean.subtract(tmean).divide(tmean));
    // https://en.wikipedia.org/wiki/Central_limit_theorem
    // @SuppressWarnings("unused")
    // Scalar limmean =
    Sqrt.of(RealScalar.of(n)).multiply(mean.subtract(tmean)).divide(Sqrt.of(tvar));
    Scalar dvar = Abs.of(var.subtract(tvar).divide(tvar));
    assertTrue(Scalars.lessThan(dmean, RealScalar.of(0.2)));
    assertTrue(Scalars.lessThan(dvar, RealScalar.of(0.22)));
  }

  public void testDiscrete() {
    _check(BernoulliDistribution.of(RationalScalar.of(2, 3)), 1000);
    _check(BinomialDistribution.of(10, RationalScalar.of(1, 4)), 1000);
    _check(BinomialDistribution.of(10, RationalScalar.of(7, 8)), 1000);
    _check(DiscreteUniformDistribution.of(RealScalar.of(10), RealScalar.of(30)), 1000);
    _check(GeometricDistribution.of(RealScalar.of(0.7)), 3000);
    _check(GeometricDistribution.of(RealScalar.of(0.1)), 2500);
    _check(HypergeometricDistribution.of(10, 40, 100), 1000); //
    _check(HypergeometricDistribution.of(10, 20, 31), 1000);
    _check(PoissonDistribution.of(RealScalar.of(2.5)), 1000);
  }

  public void testContinuous() {
    _check(ExponentialDistribution.of(RealScalar.of(2.5)), 2500); // failed with 2000, 2300
    _check(ExponentialDistribution.of(RealScalar.of(10)), 3000);
    _check(NormalDistribution.of(RealScalar.of(100), RealScalar.of(10)), 1000);
    _check(UniformDistribution.of(RealScalar.of(10), RealScalar.of(30)), 1000);
    _check(UniformDistribution.of(RealScalar.of(-100), RealScalar.of(-99)), 1000);
  }
}
