// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.lie.Quaternion;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;

public class UnitizeTest {
  @Test
  public void testVector() {
    Tensor tensor = Unitize.of(Tensors.vector(0, 0, 1e-3, -3, Double.NaN, 0));
    ExactTensorQ.require(tensor);
    assertEquals(tensor, Tensors.vector(0, 0, 1, 1, 1, 0));
  }

  @Test
  public void testGaussScalar() {
    assertEquals(Unitize.FUNCTION.apply(GaussScalar.of(0, 13)), RealScalar.ZERO);
    assertEquals(Unitize.FUNCTION.apply(GaussScalar.of(2, 7)), RealScalar.ONE);
  }

  @Test
  public void testQuantity() {
    assertEquals(Unitize.FUNCTION.apply(Quantity.of(0, "s*m^3")), RealScalar.ZERO);
    assertEquals(Unitize.FUNCTION.apply(Quantity.of(0.123, "s^-2*m^3")), RealScalar.ONE);
  }

  @Test
  public void testQuaternion() {
    assertEquals(Unitize.FUNCTION.apply(Quaternion.of(0, 0, 0.0, 0)), RealScalar.ZERO);
    assertEquals(Unitize.FUNCTION.apply(Quaternion.of(0, 0, 0.3, 0)), RealScalar.ONE);
  }
}
