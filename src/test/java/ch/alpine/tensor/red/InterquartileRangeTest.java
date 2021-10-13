// code by gjoel
package ch.alpine.tensor.red;

import java.util.Random;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.ExponentialDistribution;
import ch.alpine.tensor.pdf.PoissonDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Log;
import ch.alpine.tensor.usr.AssertFail;
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
    Tolerance.CHOP.requireClose(InterquartileRange.of(distribution), Log.of(RealScalar.of(3)).divide(lambda));
  }

  public void testDistributionUniform() { // continuous
    Distribution distribution = UniformDistribution.of(22, 30);
    Tolerance.CHOP.requireClose(InterquartileRange.of(distribution), RealScalar.of(4));
  }

  public void testDistributionPoisson() { // discrete
    Random random = new Random(123);
    // Mathematica: InterquartileRange[PoissonDistribution[10.5]] == 5
    Scalar lambda = RealScalar.of(10.5);
    Distribution distribution = PoissonDistribution.of(lambda);
    Scalar iqr = InterquartileRange.of(distribution);
    assertEquals(iqr, RealScalar.of(5));
    Tensor vector = RandomVariate.of(distribution, random, 1000);
    Scalar test = InterquartileRange.of(vector);
    assertTrue(Clips.interval(4, 6).isInside(test));
  }

  public void testEmptyFail() {
    AssertFail.of(() -> InterquartileRange.of(Tensors.empty()));
  }

  public void testMatrixFail() {
    AssertFail.of(() -> InterquartileRange.of(IdentityMatrix.of(5)));
  }
}
