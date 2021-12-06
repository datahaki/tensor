// code by jph
package ch.alpine.tensor.mat.re;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class DeterminantTest extends TestCase {
  public void testUnitsSingle() {
    Tensor tensor = Tensors.fromString("{{1[m], 2}, {4, 5[m]}, {3, 5}}");
    AssertFail.of(() -> Det.of(tensor));
  }

  public void testUnitsMixed() {
    Tensor tensor = Tensors.fromString("{{1[m], 2}, {4, 5[s]}, {3, 5}}");
    AssertFail.of(() -> Det.of(tensor));
    // assertEquals(Det.of(tensor), Quantity.of(0, ""));
  }

  public void testFailMatrixQ() {
    Tensor tensor = Tensors.fromString("{{1, 2, 3}, {4, 5}}");
    AssertFail.of(() -> Det.of(tensor));
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
