// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ConstantArray;

class MatrixQTest {
  @Test
  public void testEmpty() {
    assertFalse(MatrixQ.of(Tensors.fromString("{}")));
    assertTrue(MatrixQ.of(Tensors.fromString("{{}}")));
    assertTrue(MatrixQ.of(Tensors.fromString("{{}, {}}")));
  }

  @Test
  public void testScalar() {
    assertFalse(MatrixQ.of(RealScalar.ONE));
    assertFalse(MatrixQ.of(ComplexScalar.I));
  }

  @Test
  public void testVector() {
    assertFalse(MatrixQ.of(Tensors.vector(2, 3, 1)));
  }

  @Test
  public void testMatrix() {
    assertTrue(MatrixQ.of(Tensors.fromString("{{1}}")));
    assertTrue(MatrixQ.of(Tensors.fromString("{{1, 1, 3}, {7, 2, 9}}")));
    assertTrue(MatrixQ.of(ConstantArray.of(DoubleScalar.INDETERMINATE, 4, 3)));
    assertFalse(MatrixQ.of(Tensors.fromString("{{1, 1}, {7, 2, 9}}")));
  }

  @Test
  public void testMatrixSize() {
    assertTrue(MatrixQ.ofSize(Tensors.fromString("{{1}}"), 1, 1));
    assertFalse(MatrixQ.ofSize(Tensors.fromString("{{1}}"), 1, 2));
    assertFalse(MatrixQ.ofSize(Tensors.fromString("{{1}}"), 2, 1));
    assertTrue(MatrixQ.ofSize(Tensors.fromString("{{1, 1, 3}, {7, 2, 9}}"), 2, 3));
    MatrixQ.requireSize(Tensors.fromString("{{1, 1, 3}, {7, 2, 9}}"), 2, 3);
    assertFalse(MatrixQ.ofSize(Tensors.fromString("{{1, 1}, {7, 2, 9}}"), 2, 2));
    assertTrue(MatrixQ.ofSize(HilbertMatrix.of(3, 4), 3, 4));
    assertTrue(MatrixQ.ofSize(HilbertMatrix.of(2, 7), 2, 7));
    MatrixQ.requireSize(HilbertMatrix.of(2, 7), 2, 7);
    assertFalse(MatrixQ.ofSize(HilbertMatrix.of(2, 7), 2, 6));
    assertFalse(MatrixQ.ofSize(HilbertMatrix.of(2, 7), 3, 7));
  }

  @Test
  public void testArrayWithDimensions() {
    Tensor tensor = Tensors.fromString("{{1, 2}, {3, {4}}, {5, 6}}");
    assertFalse(MatrixQ.ofSize(tensor, 3, 2));
  }

  @Test
  public void testAd() {
    assertFalse(MatrixQ.of(Array.zeros(3, 3, 3)));
  }

  @Test
  public void testElseThrow() {
    assertThrows(TensorRuntimeException.class, () -> MatrixQ.require(Tensors.vector(1, 2, 3)));
  }

  @Test
  public void testRequireNullThrow() {
    MatrixQ.require(HilbertMatrix.of(2, 3));
    assertThrows(NullPointerException.class, () -> MatrixQ.require(null));
  }

  @Test
  public void testOfNullThrow() {
    assertThrows(NullPointerException.class, () -> MatrixQ.of(null));
  }

  @Test
  public void testRequireSize() {
    MatrixQ.requireSize(IdentityMatrix.of(3), 3, 3);
    assertThrows(TensorRuntimeException.class, () -> MatrixQ.requireSize(IdentityMatrix.of(3), 3, 4));
  }
}
