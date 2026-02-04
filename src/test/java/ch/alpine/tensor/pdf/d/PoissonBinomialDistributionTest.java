// code by jph
package ch.alpine.tensor.pdf.d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;

class PoissonBinomialDistributionTest {
  @Test
  void testEmpty() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(PoissonBinomialDistribution.of(Tensors.empty()));
    Tensor samples = RandomVariate.of(distribution, 10);
    assertEquals(samples, Array.zeros(10));
    assertEquals(Mean.of(distribution), RealScalar.ZERO);
    assertEquals(Variance.of(distribution), RealScalar.ZERO);
  }

  @Test
  void testZeros() {
    Distribution distribution = PoissonBinomialDistribution.of(Array.zeros(4));
    Tensor samples = RandomVariate.of(distribution, 10);
    assertEquals(samples, Array.zeros(10));
    assertEquals(Mean.of(distribution), RealScalar.ZERO);
    assertEquals(Variance.of(distribution), RealScalar.ZERO);
  }

  @Test
  void testOnes() {
    Distribution distribution = PoissonBinomialDistribution.of(Tensors.vector(1, 1, 1, 1));
    Tensor samples = RandomVariate.of(distribution, 10);
    assertEquals(samples, Array.of(_ -> RealScalar.of(4), 10));
    assertEquals(Mean.of(distribution), RealScalar.of(4));
    assertEquals(Variance.of(distribution), RealScalar.ZERO);
  }

  @Test
  void testMixed() {
    Distribution distribution = PoissonBinomialDistribution.of(Tensors.vector(1, 0, 0.2, 0.3, 0.4, 0.5, 1, 0, 0));
    RandomVariate.of(distribution, 10);
    Tolerance.CHOP.requireClose(Mean.of(distribution), RealScalar.of(3.4));
    Tolerance.CHOP.requireClose(Variance.of(distribution), RealScalar.of(0.86));
    // DiscreteDistribution discreteDistribution = (DiscreteDistribution) distribution;
    // assertEquals(discreteDistribution.lowerBound(), 2);
  }

  @Test
  void testProbFail() {
    Distribution distribution = PoissonBinomialDistribution.of(Tensors.vector(1, 1, 1, 1));
    assertTrue(distribution.toString().startsWith("PoissonBinomialDistribution["));
  }

  @Test
  void testCdfFail() {
    Distribution distribution = PoissonBinomialDistribution.of(Tensors.vector(0.1, 0.3, 1, 0.1, 0.5));
    assertThrows(IllegalArgumentException.class, () -> CDF.of(distribution));
  }

  @Test
  void testFail() {
    assertThrows(IllegalArgumentException.class, () -> PoissonBinomialDistribution.of(RealScalar.ZERO));
    assertThrows(ClassCastException.class, () -> PoissonBinomialDistribution.of(IdentityMatrix.of(3)));
  }

  @Test
  void testFailInvalid() {
    assertThrows(Throw.class, () -> PoissonBinomialDistribution.of(Tensors.vector(1, 1, 1, 1, 2, 0)));
    assertThrows(Throw.class, () -> PoissonBinomialDistribution.of(Tensors.vector(1, 1, 1, 1, -1, 1)));
  }
}
