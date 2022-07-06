// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.qty.Quantity;

class SoftplusTest {
  @Test
  void testZero() {
    Scalar s = Softplus.FUNCTION.apply(RealScalar.ZERO);
    assertEquals(s, RealScalar.of(0.6931471805599453));
  }

  @Test
  void testOuter() {
    assertEquals(Softplus.FUNCTION.apply(RealScalar.of(+1000000)), RealScalar.of(1000000));
    assertEquals(Softplus.FUNCTION.apply(RealScalar.of(-1000000)), RealScalar.of(0));
  }

  @Test
  void testTensor() {
    Scalar s0 = Softplus.FUNCTION.apply(RealScalar.ZERO);
    Scalar s1 = Softplus.FUNCTION.apply(RealScalar.ONE);
    Tensor tensor = Softplus.of(Tensors.vector(0, 1));
    assertEquals(tensor, Tensors.of(s0, s1));
  }

  @Test
  void testQuantityFail() {
    assertThrows(Throw.class, () -> Softplus.FUNCTION.apply(Quantity.of(1, "s")));
  }
}
