// code by jph
package ch.alpine.tensor.spa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class SparseArrayToStringTest {
  @Test
  public void testSimple() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}");
    Tensor sparse = TestHelper.of(tensor);
    String string = sparse.toString();
    assertTrue(string.startsWith(SparseArray.class.getSimpleName()));
  }

  @Test
  public void testVisibility() {
    assertEquals(SparseArrayToString.class.getModifiers() & 1, 0);
  }
}
