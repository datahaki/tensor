// code by jph
package ch.ethz.idsc.tensor;

import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class TensorsMessageTest extends TestCase {
  public void testSimple() {
    assertEquals(Tensors.message(Pi.VALUE), "3.141592653589793");
    assertEquals(Tensors.message(RationalScalar.HALF, Tensors.vector(1, 2)), "1/2; {1, 2}");
    assertEquals(Tensors.message(RationalScalar.HALF, null, Quantity.of(3, "d")), "1/2; null; 3[d]");
  }

  public void testSmallMatrix() {
    assertTrue(Tensors.message(HilbertMatrix.of(4, 3)).startsWith("T[4, 3]={"));
    assertEquals(Tensors.message(HilbertMatrix.of(2, 3)), "{{1, 1/2, 1/3}, {1/2, 1/3, 1/4}}");
    assertEquals(Tensors.message(Array.zeros(10, 10)), "T[10, 10]");
  }
}
