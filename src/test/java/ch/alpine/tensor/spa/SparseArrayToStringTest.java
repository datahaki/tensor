// code by jph
package ch.alpine.tensor.spa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class SparseArrayToStringTest {
  @Test
  void testSimple() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}");
    Tensor sparse = TensorToSparseArray.of(tensor);
    String string = sparse.toString();
    assertTrue(string.startsWith(SparseArray.class.getSimpleName()));
  }

  @Test
  void testVisibility() {
    assertEquals(SparseArrayToString.class.getModifiers() & 1, 0);
  }

  @Test
  void testFail() {
    assertThrows(Exception.class, () -> new SparseArrayToString(-1));
    assertThrows(Exception.class, () -> new SparseArrayToString(2));
  }
}
