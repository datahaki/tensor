// code by jph
package ch.alpine.tensor.pdf;

import java.io.IOException;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class PoissonBinomialDistributionTest extends TestCase {
  public void testEmpty() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(PoissonBinomialDistribution.of(Tensors.empty()));
    Tensor samples = RandomVariate.of(distribution, 10);
    assertEquals(samples, Array.zeros(10));
    assertEquals(Mean.of(distribution), RealScalar.ZERO);
    assertEquals(Variance.of(distribution), RealScalar.ZERO);
  }

  public void testZeros() {
    Distribution distribution = PoissonBinomialDistribution.of(Array.zeros(4));
    Tensor samples = RandomVariate.of(distribution, 10);
    assertEquals(samples, Array.zeros(10));
    assertEquals(Mean.of(distribution), RealScalar.ZERO);
    assertEquals(Variance.of(distribution), RealScalar.ZERO);
  }

  public void testOnes() {
    Distribution distribution = PoissonBinomialDistribution.of(Tensors.vector(1, 1, 1, 1));
    Tensor samples = RandomVariate.of(distribution, 10);
    assertEquals(samples, Array.of(l -> RealScalar.of(4), 10));
    assertEquals(Mean.of(distribution), RealScalar.of(4));
    assertEquals(Variance.of(distribution), RealScalar.ZERO);
  }

  public void testMixed() {
    Distribution distribution = PoissonBinomialDistribution.of(Tensors.vector(1, 0, 0.2, 0.3, 0.4, 0.5, 1, 0, 0));
    RandomVariate.of(distribution, 10);
    Chop._12.requireClose(Mean.of(distribution), RealScalar.of(3.4));
    Chop._12.requireClose(Variance.of(distribution), RealScalar.of(0.86));
    DiscreteDistribution discreteDistribution = (DiscreteDistribution) distribution;
    assertEquals(discreteDistribution.lowerBound(), 2);
  }

  public void testProbFail() {
    Distribution distribution = PoissonBinomialDistribution.of(Tensors.vector(1, 1, 1, 1));
    assertTrue(distribution.toString().startsWith("PoissonBinomialDistribution["));
    PDF pdf = PDF.of(distribution);
    try {
      pdf.at(RealScalar.of(2));
      fail();
    } catch (Exception exception) {
      assertTrue(UnsupportedOperationException.class.isInstance(exception));
    }
    DiscreteDistribution discreteDistribution = (DiscreteDistribution) distribution;
    try {
      discreteDistribution.p_equals(3);
      fail();
    } catch (Exception exception) {
      assertTrue(UnsupportedOperationException.class.isInstance(exception));
    }
  }

  public void testFail() {
    AssertFail.of(() -> PoissonBinomialDistribution.of(RealScalar.ZERO));
    AssertFail.of(() -> PoissonBinomialDistribution.of(IdentityMatrix.of(3)));
  }

  public void testFailInvalid() {
    AssertFail.of(() -> PoissonBinomialDistribution.of(Tensors.vector(1, 1, 1, 1, 2, 0)));
    AssertFail.of(() -> PoissonBinomialDistribution.of(Tensors.vector(1, 1, 1, 1, -1, 1)));
  }
}
