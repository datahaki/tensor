// code by jph
package ch.alpine.tensor;

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
}
