// code by jph
package ch.alpine.tensor.nrm;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.mat.HilbertMatrix;

public class NormalizeFailTest {
  @Test
  public void testEmpty() {
    assertThrows(NoSuchElementException.class, () -> Vector2Norm.NORMALIZE.apply(Tensors.empty()));
  }

  @Test
  public void testZeros() {
    assertThrows(ArithmeticException.class, () -> Vector2Norm.NORMALIZE.apply(Array.zeros(10)));
  }

  @Test
  public void testFail1() {
    TensorUnaryOperator normalize = Vector1Norm.NORMALIZE;
    assertThrows(ArithmeticException.class, () -> normalize.apply(Tensors.vector(0, 0, 0, 0)));
  }

  @Test
  public void testNormalizePositiveInfinity() {
    Tensor vector = Tensors.of(DoubleScalar.POSITIVE_INFINITY, RealScalar.ONE);
    assertThrows(TensorRuntimeException.class, () -> Vector2Norm.NORMALIZE.apply(vector));
    assertThrows(TensorRuntimeException.class, () -> NormalizeUnlessZero.with(Vector2Norm::of).apply(vector));
  }

  @Test
  public void testNormalizeNegativeInfinity() {
    Tensor vector = Tensors.of(DoubleScalar.NEGATIVE_INFINITY, RealScalar.ONE, DoubleScalar.POSITIVE_INFINITY);
    assertThrows(TensorRuntimeException.class, () -> Vector2Norm.NORMALIZE.apply(vector));
  }

  @Test
  public void testNormalizeNaN() {
    Tensor vector = Tensors.of(RealScalar.ONE, DoubleScalar.INDETERMINATE, RealScalar.ONE);
    assertThrows(TensorRuntimeException.class, () -> Vector2Norm.NORMALIZE.apply(vector));
  }

  @Test
  public void testScalarFail() {
    assertThrows(TensorRuntimeException.class, () -> Vector2Norm.NORMALIZE.apply(RealScalar.ONE));
  }

  @Test
  public void testMatrixFail() {
    assertThrows(ClassCastException.class, () -> Vector2Norm.NORMALIZE.apply(Tensors.fromString("{{1, 2}, {3, 4, 5}}")));
    assertThrows(ClassCastException.class, () -> Vector2Norm.NORMALIZE.apply(HilbertMatrix.of(3)));
  }
}
