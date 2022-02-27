// code by jph
package ch.alpine.tensor.spa;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.Pi;
import junit.framework.TestCase;

public class NormalTest extends TestCase {
  public void testSimple() {
    Tensor tensor = Tensors.fromString("{{1}, 2}");
    Tensor result = Normal.of(tensor);
    assertEquals(tensor, result);
  }

  public void testScalar() {
    assertEquals(Normal.of(Pi.VALUE), Pi.VALUE);
  }

  public void testMixed() {
    Tensor mixed = Tensors.of( //
        SparseArray.of(RealScalar.ZERO, 3), Tensors.vector(1, 2), SparseArray.of(RealScalar.ZERO, 3));
    assertTrue(mixed.get(0) instanceof SparseArray);
    assertEquals(Normal.of(mixed).toString(), "{{0, 0, 0}, {1, 2}, {0, 0, 0}}");
  }
}
