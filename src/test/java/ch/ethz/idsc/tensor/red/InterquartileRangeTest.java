// code by gjoel
package ch.ethz.idsc.tensor.red;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.ExponentialDistribution;
import ch.ethz.idsc.tensor.pdf.PoissonDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class InterquartileRangeTest extends TestCase {
  public void testSamples() {
    Tensor samples = Tensors.vector(0, 1, 2, 3, 10);
    assertEquals(InterquartileRange.of(samples), RealScalar.of(2)); // == 3 - 1
  }

  public void testMathematica() {
    assertEquals(InterquartileRange.of(Tensors.vector(1, 3, 4, 2, 5, 6)), RealScalar.of(3));
  }

  public void testDistributionExp() { // continuous
    Scalar lambda = RealScalar.of(5);
    Distribution distribution = ExponentialDistribution.of(lambda);
    Chop._12.requireClose(InterquartileRange.of(distribution), Log.of(RealScalar.of(3)).divide(lambda));
  }

  public void testDistributionUniform() { // continuous
    Distribution distribution = UniformDistribution.of(22, 30);
    Chop._12.requireClose(InterquartileRange.of(distribution), RealScalar.of(4));
  }

  public void testDistributionPoisson() { // discrete
    // Mathematica: InterquartileRange[PoissonDistribution[10.5]] == 5
    Scalar lambda = RealScalar.of(10.5);
    Distribution distribution = PoissonDistribution.of(lambda);
    Scalar iqr = InterquartileRange.of(distribution);
    assertEquals(iqr, RealScalar.of(5));
    Tensor random = RandomVariate.of(distribution, 1100);
    Scalar test = InterquartileRange.of(random);
    assertTrue(Clips.interval(4, 6).isInside(test));
  }

  public void testEmptyFail() {
    AssertFail.of(() -> InterquartileRange.of(Tensors.empty()));
  }

  public void testMatrixFail() {
    AssertFail.of(() -> InterquartileRange.of(IdentityMatrix.of(5)));
  }
}
