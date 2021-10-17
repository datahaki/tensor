// code by jph
package ch.alpine.tensor.spa;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class SparseArrayToStringTest extends TestCase {
  public void testSimple() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}");
    Tensor sparse = SparseArrays.of(tensor, RealScalar.ZERO);
    String string = sparse.toString();
    assertTrue(string.startsWith(SparseArray.class.getSimpleName()));
  }

  public void testVisibility() {
    assertEquals(SparseArrayToString.class.getModifiers() & 1, 0);
  }
}
