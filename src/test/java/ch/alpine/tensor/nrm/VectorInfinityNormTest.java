// code by jph
package ch.alpine.tensor.nrm;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import junit.framework.TestCase;

public class VectorInfinityNormTest extends TestCase {
  public void testQuantity() {
    Scalar qs1 = Quantity.of(-3, "m");
    Scalar qs2 = Quantity.of(-4, "m");
    Scalar qs3 = Quantity.of(4, "m");
    Tensor vec = Tensors.of(qs1, qs2);
    assertEquals(VectorInfinityNorm.of(vec), qs3);
    assertEquals(VectorInfinityNorm.NORMALIZE.apply(vec), Tensors.fromString("{-3/4, -1}"));
  }

  public void testQuantityMixed() {
    Scalar qs1 = Quantity.of(-3, "m");
    Scalar qs2 = Quantity.of(2, "m");
    Tensor vec = Tensors.of(qs1, qs2);
    Scalar nin = VectorInfinityNorm.of(vec);
    Scalar act = Quantity.of(3, "m");
    assertEquals(nin, act);
  }
}
