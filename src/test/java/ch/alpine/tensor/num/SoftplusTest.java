// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class SoftplusTest extends TestCase {
  public void testZero() {
    Scalar s = Softplus.FUNCTION.apply(RealScalar.ZERO);
    assertEquals(s, RealScalar.of(0.6931471805599453));
  }

  public void testOuter() {
    assertEquals(Softplus.FUNCTION.apply(RealScalar.of(+1000000)), RealScalar.of(1000000));
    assertEquals(Softplus.FUNCTION.apply(RealScalar.of(-1000000)), RealScalar.of(0));
  }

  public void testTensor() {
    Scalar s0 = Softplus.FUNCTION.apply(RealScalar.ZERO);
    Scalar s1 = Softplus.FUNCTION.apply(RealScalar.ONE);
    Tensor tensor = Softplus.of(Tensors.vector(0, 1));
    assertEquals(tensor, Tensors.of(s0, s1));
  }

  public void testQuantityFail() {
    AssertFail.of(() -> Softplus.FUNCTION.apply(Quantity.of(1, "s")));
  }
}
