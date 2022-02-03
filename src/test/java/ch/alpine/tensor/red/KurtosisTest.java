// code by jph
package ch.alpine.tensor.red;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.TrapezoidalDistribution;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class KurtosisTest extends TestCase {
  public void testMathematica() {
    Tensor tensor = Tensors.vector(10, 2, 3, 4, 1);
    Scalar result = Kurtosis.of(tensor);
    assertEquals(result, Scalars.fromString("697/250")); // confirmed in mathematica
  }

  public void testTrapezoidal() {
    Distribution distribution = TrapezoidalDistribution.of(4, 5, 7, 10);
    Scalar scalar = Kurtosis.of(distribution);
    Scalar check = CentralMoment.of(distribution, 4).divide(Variance.of(distribution)).divide(Variance.of(distribution));
    assertEquals(scalar, check);
    ExactScalarQ.require(check);
  }

  public void testUniform() {
    Distribution distribution = UniformDistribution.of(-3, -1);
    Scalar scalar = Kurtosis.of(distribution);
    Scalar check = CentralMoment.of(distribution, 4).divide(Variance.of(distribution)).divide(Variance.of(distribution));
    assertEquals(scalar, check);
    ExactScalarQ.require(scalar);
    ExactScalarQ.require(check);
  }

  public void testFailScalar() {
    AssertFail.of(() -> Kurtosis.of(RealScalar.ONE));
  }

  public void testFailMatrix() {
    AssertFail.of(() -> Kurtosis.of(HilbertMatrix.of(3)));
  }
}
