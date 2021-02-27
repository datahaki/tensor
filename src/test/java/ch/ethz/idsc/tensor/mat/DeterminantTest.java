// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.lie.LeviCivitaTensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class DeterminantTest extends TestCase {
  public void testUnitsSingle() {
    Tensor table = Tensors.fromString("{{1[m], 2}, {4, 5[m]}, {3, 5}}");
    assertEquals(Det.of(table), Quantity.of(0, "m"));
  }

  public void testUnitsMixed() {
    Tensor table = Tensors.fromString("{{1[m], 2}, {4, 5[s]}, {3, 5}}");
    assertEquals(Det.of(table), Quantity.of(0, ""));
  }

  public void testFailMatrixQ() {
    Tensor table = Tensors.fromString("{{1, 2, 3}, {4, 5}}");
    AssertFail.of(() -> Det.of(table));
  }

  public void testFailNonArray() {
    Tensor matrix = HilbertMatrix.of(4);
    matrix.set(Tensors.vector(1, 2, 3), 1, 2);
    AssertFail.of(() -> Det.of(matrix));
  }

  public void testFailRank3() {
    AssertFail.of(() -> Det.of(LeviCivitaTensor.of(3)));
  }

  public void testFailRank3b() {
    AssertFail.of(() -> Det.of(Array.zeros(2, 2, 3)));
  }

  public void testFailNull() {
    AssertFail.of(() -> Det.of(null));
    AssertFail.of(() -> Det.of(HilbertMatrix.of(3), null));
  }
}
