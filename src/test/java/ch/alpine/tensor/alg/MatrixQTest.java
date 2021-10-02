// code by jph
package ch.alpine.tensor.alg;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class MatrixQTest extends TestCase {
  public void testEmpty() {
    assertFalse(MatrixQ.of(Tensors.fromString("{}")));
    assertTrue(MatrixQ.of(Tensors.fromString("{{}}")));
    assertTrue(MatrixQ.of(Tensors.fromString("{{}, {}}")));
  }

  public void testScalar() {
    assertFalse(MatrixQ.of(RealScalar.ONE));
    assertFalse(MatrixQ.of(ComplexScalar.I));
  }

  public void testVector() {
    assertFalse(MatrixQ.of(Tensors.vector(2, 3, 1)));
  }

  public void testMatrix() {
    assertTrue(MatrixQ.of(Tensors.fromString("{{1}}")));
    assertTrue(MatrixQ.of(Tensors.fromString("{{1, 1, 3}, {7, 2, 9}}")));
    assertTrue(MatrixQ.of(ConstantArray.of(DoubleScalar.INDETERMINATE, 4, 3)));
    assertFalse(MatrixQ.of(Tensors.fromString("{{1, 1}, {7, 2, 9}}")));
  }

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

  public void testArrayWithDimensions() {
    Tensor tensor = Tensors.fromString("{{1, 2}, {3, {4}}, {5, 6}}");
    assertFalse(MatrixQ.ofSize(tensor, 3, 2));
  }

  public void testAd() {
    assertFalse(MatrixQ.of(Array.zeros(3, 3, 3)));
  }

  public void testElseThrow() {
    AssertFail.of(() -> MatrixQ.require(Tensors.vector(1, 2, 3)));
  }

  public void testRequireNullThrow() {
    MatrixQ.require(HilbertMatrix.of(2, 3));
    AssertFail.of(() -> MatrixQ.require(null));
  }

  public void testOfNullThrow() {
    AssertFail.of(() -> MatrixQ.of(null));
  }

  public void testRequireSize() {
    MatrixQ.requireSize(IdentityMatrix.of(3), 3, 3);
    AssertFail.of(() -> MatrixQ.requireSize(IdentityMatrix.of(3), 3, 4));
  }
}
