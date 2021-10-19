// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.lie.LehmerTensor;
import ch.alpine.tensor.mat.gr.IdempotentQ;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.spa.SparseArray;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class IdentityMatrixTest extends TestCase {
  public void testOneQuantity() {
    Tensor matrix = DiagonalMatrix.of(2, Quantity.of(1, "s"));
    assertEquals(matrix, Tensors.fromString("{{1[s], 0[s]}, {0[s], 1[s]}}"));
  }

  public void testHilbertMatrix() {
    assertEquals(IdentityMatrix.of(HilbertMatrix.of(3)), IdentityMatrix.of(3));
  }

  public void testSparse() {
    int n = 7;
    Tensor matrix = IdentityMatrix.sparse(n);
    assertTrue(matrix instanceof SparseArray);
    Timing timing0 = Timing.started();
    Tensor square = matrix.dot(matrix);
    timing0.stop();
    assertTrue(square instanceof SparseArray);
    assertEquals(square, matrix);
    assertEquals(square, IdentityMatrix.of(n));
    IdempotentQ.of(matrix);
    Tensor matrow = matrix.get(1);
    assertTrue(matrow instanceof SparseArray);
    Tensor squrow = square.get(1);
    assertTrue(squrow instanceof SparseArray);
    // Tensor re = IdentityMatrix.of(n);
    // Timing timing1 = Timing.started();
    // re.dot(re);
    // timing1.stop();
    // System.out.println(timing0.seconds());
    // System.out.println(timing1.seconds());
  }

  public void testSparseFail() {
    AssertFail.of(() -> IdentityMatrix.sparse(0));
    AssertFail.of(() -> IdentityMatrix.sparse(-1));
  }

  public void testFailZero() {
    AssertFail.of(() -> IdentityMatrix.of(0));
  }

  public void testFailNegative() {
    AssertFail.of(() -> IdentityMatrix.of(-3));
  }

  public void testFailOneZero() {
    AssertFail.of(() -> DiagonalMatrix.of(0, Quantity.of(1, "s")));
  }

  public void testFailOneNegative() {
    AssertFail.of(() -> DiagonalMatrix.of(-3, Quantity.of(1, "s")));
  }

  public void testEmptyFail() {
    AssertFail.of(() -> IdentityMatrix.of(Tensors.empty()));
    AssertFail.of(() -> IdentityMatrix.of(Tensors.fromString("{{}}")));
    AssertFail.of(() -> IdentityMatrix.of(LehmerTensor.of(3)));
  }
}
