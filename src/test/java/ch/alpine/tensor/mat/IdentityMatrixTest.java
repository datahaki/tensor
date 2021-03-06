// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.lie.LehmerTensor;
import ch.alpine.tensor.qty.Quantity;
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
