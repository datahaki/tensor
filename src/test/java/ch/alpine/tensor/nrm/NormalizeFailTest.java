// code by jph
package ch.alpine.tensor.nrm;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.usr.AssertFail;

public class NormalizeFailTest {
  @Test
  public void testEmpty() {
    AssertFail.of(() -> Vector2Norm.NORMALIZE.apply(Tensors.empty()));
  }

  @Test
  public void testZeros() {
    AssertFail.of(() -> Vector2Norm.NORMALIZE.apply(Array.zeros(10)));
  }

  @Test
  public void testFail1() {
    TensorUnaryOperator normalize = Vector1Norm.NORMALIZE;
    AssertFail.of(() -> normalize.apply(Tensors.vector(0, 0, 0, 0)));
  }

  @Test
  public void testNormalizePositiveInfinity() {
    Tensor vector = Tensors.of(DoubleScalar.POSITIVE_INFINITY, RealScalar.ONE);
    AssertFail.of(() -> Vector2Norm.NORMALIZE.apply(vector));
    AssertFail.of(() -> NormalizeUnlessZero.with(Vector2Norm::of).apply(vector));
  }

  @Test
  public void testNormalizeNegativeInfinity() {
    Tensor vector = Tensors.of(DoubleScalar.NEGATIVE_INFINITY, RealScalar.ONE, DoubleScalar.POSITIVE_INFINITY);
    AssertFail.of(() -> Vector2Norm.NORMALIZE.apply(vector));
  }

  @Test
  public void testNormalizeNaN() {
    Tensor vector = Tensors.of(RealScalar.ONE, DoubleScalar.INDETERMINATE, RealScalar.ONE);
    AssertFail.of(() -> Vector2Norm.NORMALIZE.apply(vector));
  }

  @Test
  public void testScalarFail() {
    AssertFail.of(() -> Vector2Norm.NORMALIZE.apply(RealScalar.ONE));
  }

  @Test
  public void testMatrixFail() {
    AssertFail.of(() -> Vector2Norm.NORMALIZE.apply(Tensors.fromString("{{1, 2}, {3, 4, 5}}")));
    AssertFail.of(() -> Vector2Norm.NORMALIZE.apply(HilbertMatrix.of(3)));
  }
}
