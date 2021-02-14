// code by jph
package ch.ethz.idsc.tensor.nrm;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class VectorNormInfinityTest extends TestCase {
  public void testQuantity() {
    Scalar qs1 = Quantity.of(-3, "m");
    Scalar qs2 = Quantity.of(-4, "m");
    Scalar qs3 = Quantity.of(4, "m");
    Tensor vec = Tensors.of(qs1, qs2);
    assertEquals(VectorNormInfinity.of(vec), qs3);
    assertEquals(VectorNormInfinity.NORMALIZE.apply(vec), Tensors.fromString("{-3/4, -1}"));
  }

  public void testQuantityMixed() {
    Scalar qs1 = Quantity.of(-3, "m");
    Scalar qs2 = Quantity.of(2, "m");
    Tensor vec = Tensors.of(qs1, qs2);
    Scalar nin = VectorNormInfinity.of(vec);
    Scalar act = Quantity.of(3, "m");
    assertEquals(nin, act);
  }
}
