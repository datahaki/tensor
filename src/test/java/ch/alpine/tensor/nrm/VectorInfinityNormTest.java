// code by jph
package ch.alpine.tensor.nrm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;

class VectorInfinityNormTest {
  @Test
  public void testOneInfNorm1() {
    Tensor a = Tensors.vector(3, -4);
    assertEquals(Vector1Norm.of(a), Scalars.fromString("7"));
    assertEquals(VectorInfinityNorm.of(a), Scalars.fromString("4"));
  }

  @Test
  public void testQuantity() {
    Scalar qs1 = Quantity.of(-3, "m");
    Scalar qs2 = Quantity.of(-4, "m");
    Scalar qs3 = Quantity.of(4, "m");
    Tensor vec = Tensors.of(qs1, qs2);
    assertEquals(VectorInfinityNorm.of(vec), qs3);
    assertEquals(VectorInfinityNorm.NORMALIZE.apply(vec), Tensors.fromString("{-3/4, -1}"));
  }

  @Test
  public void testQuantityMixed() {
    Scalar qs1 = Quantity.of(-3, "m");
    Scalar qs2 = Quantity.of(2, "m");
    Tensor vec = Tensors.of(qs1, qs2);
    Scalar nin = VectorInfinityNorm.of(vec);
    Scalar act = Quantity.of(3, "m");
    assertEquals(nin, act);
  }
}
