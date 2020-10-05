// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class IdentityMatrixTest extends TestCase {
  public void testOneQuantity() {
    Tensor matrix = DiagonalMatrix.of(2, Quantity.of(1, "s"));
    assertEquals(matrix, Tensors.fromString("{{1[s], 0[s]}, {0[s], 1[s]}}"));
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
}
