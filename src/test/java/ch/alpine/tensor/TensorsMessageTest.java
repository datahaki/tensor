// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;

class TensorsMessageTest {
  @Test
  void testSimple() {
    assertEquals(Tensors.message(Pi.VALUE), "3.141592653589793");
    assertEquals(Tensors.message(RationalScalar.HALF, Tensors.vector(1, 2)), "1/2; {1, 2}");
    assertEquals(Tensors.message(RationalScalar.HALF, null, Quantity.of(3, "d")), "1/2; null; 3[d]");
  }

  @Test
  void testSmallMatrix() {
    assertTrue(Tensors.message(HilbertMatrix.of(4, 3)).startsWith("T[4, 3]={"));
    assertEquals(Tensors.message(HilbertMatrix.of(2, 3)), "{{1, 1/2, 1/3}, {1/2, 1/3, 1/4}}");
    assertEquals(Tensors.message(Array.zeros(10, 10)), "T[10, 10]");
  }
}
