// code by jph
package ch.alpine.tensor.pdf;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.pdf.c.BirnbaumSaundersDistribution;
import ch.alpine.tensor.pdf.c.ExponentialDistribution;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.pdf.d.BernoulliDistribution;
import ch.alpine.tensor.pdf.d.BinomialDistribution;
import ch.alpine.tensor.pdf.d.BorelTannerDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.pdf.d.GeometricDistribution;
import ch.alpine.tensor.pdf.d.HypergeometricDistribution;
import ch.alpine.tensor.pdf.d.PoissonDistribution;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.pow.Sqrt;

class DistributionTest {
  private static void _check(Distribution distribution, int n) {
    Tensor vector = RandomVariate.of(distribution, n);
    Scalar mean = Mean.ofVector(vector); // measured mean
    Scalar var = Variance.ofVector(vector); // measured variance
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

  @Test
  void testBernoulliDistribution() {
    _check(BernoulliDistribution.of(RationalScalar.of(2, 3)), 1000);
  }

  @Test
  void testBinomialDistribution() {
    _check(BinomialDistribution.of(10, RationalScalar.of(1, 4)), 1000);
    _check(BinomialDistribution.of(10, RationalScalar.of(7, 8)), 1000);
  }

  @Test
  void testDiscreteUniform() {
    _check(DiscreteUniformDistribution.of(10, 30), 1000);
  }

  @Test
  void testGeometricDistribution() {
    _check(GeometricDistribution.of(0.7), 3000);
    _check(GeometricDistribution.of(0.1), 2500);
  }

  @Test
  void testHypergeometricDistribution() {
    _check(HypergeometricDistribution.of(10, 40, 100), 1000); //
    _check(HypergeometricDistribution.of(10, 20, 31), 1000);
  }

  @Test
  void testPoissonDistribution() {
    _check(PoissonDistribution.of(2.5), 1000);
  }

  @Test
  void testBorelTannerDistribution() {
    _check(BorelTannerDistribution.of(0.3, 10), 1000);
    _check(BorelTannerDistribution.of(0.8, 10), 2000);
  }

  @Test
  void testExponentialDistribution() {
    _check(ExponentialDistribution.of(2.5), 3000); // failed with 2000, 2300, 2500
    _check(ExponentialDistribution.of(10), 3000);
  }

  @Test
  void testNormalDistribution() {
    _check(NormalDistribution.of(100, 10), 1000);
  }

  @Test
  void testUniformDistribution() {
    _check(UniformDistribution.of(10, 30), 1000);
    _check(UniformDistribution.of(-100, -99), 1000);
  }

  @Test
  void testBirnbaumSaundersDistribution() {
    _check(BirnbaumSaundersDistribution.of(1, 2), 1800);
    _check(BirnbaumSaundersDistribution.of(0.3, 0.7), 1400);
  }
}
