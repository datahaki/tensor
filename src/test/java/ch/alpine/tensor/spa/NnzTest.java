// code by jph
package ch.alpine.tensor.spa;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class NnzTest extends TestCase {
  public void testSimple() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}");
    SparseArray sparseArray = (SparseArray) SparseArrays.of(tensor, RealScalar.ZERO);
    assertEquals(Nnz.of(sparseArray), 8);
  }
}
