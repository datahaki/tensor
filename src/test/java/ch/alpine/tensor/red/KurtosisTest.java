// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.c.TrapezoidalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;

class KurtosisTest {
  @Test
  public void testMathematica() {
    Tensor tensor = Tensors.vector(10, 2, 3, 4, 1);
    Scalar result = Kurtosis.of(tensor);
    assertEquals(result, Scalars.fromString("697/250")); // confirmed in mathematica
  }

  @Test
  public void testTrapezoidal() {
    Distribution distribution = TrapezoidalDistribution.of(4, 5, 7, 10);
    Scalar scalar = Kurtosis.of(distribution);
    Scalar check = CentralMoment.of(distribution, 4).divide(Variance.of(distribution)).divide(Variance.of(distribution));
    assertEquals(scalar, check);
    ExactScalarQ.require(check);
  }

  @Test
  public void testUniform() {
    Distribution distribution = UniformDistribution.of(-3, -1);
    Scalar scalar = Kurtosis.of(distribution);
    Scalar check = CentralMoment.of(distribution, 4).divide(Variance.of(distribution)).divide(Variance.of(distribution));
    assertEquals(scalar, check);
    ExactScalarQ.require(scalar);
    ExactScalarQ.require(check);
  }

  @Test
  public void testFailScalar() {
    assertThrows(TensorRuntimeException.class, () -> Kurtosis.of(RealScalar.ONE));
  }

  @Test
  public void testFailMatrix() {
    assertThrows(ClassCastException.class, () -> Kurtosis.of(HilbertMatrix.of(3)));
  }
}
