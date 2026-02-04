// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.lie.rot.Quaternion;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;

class UnitStepTest {
  @Test
  void testRealScalar() {
    assertEquals(UnitStep.FUNCTION.apply(RealScalar.of(-0.3)), RealScalar.ZERO);
    assertEquals(UnitStep.FUNCTION.apply(RealScalar.of(0)), RealScalar.ONE);
    assertEquals(UnitStep.FUNCTION.apply(RealScalar.of(0.134)), RealScalar.ONE);
  }

  @Test
  void testPredicateQuantity() {
    assertEquals(UnitStep.FUNCTION.apply(Quantity.of(-0.3, "m")), RealScalar.ZERO);
    assertEquals(UnitStep.FUNCTION.apply(Quantity.of(0.0, "m")), RealScalar.ONE);
    assertEquals(UnitStep.FUNCTION.apply(Quantity.of(0, "m")), RealScalar.ONE);
    assertEquals(UnitStep.FUNCTION.apply(Quantity.of(1, "m")), RealScalar.ONE);
  }

  @Test
  void testGaussScalar() {
    assertEquals(UnitStep.FUNCTION.apply(GaussScalar.of(2, 7)), RealScalar.ONE);
  }

  @Test
  void testQuaternionFail() {
    Scalar scalar = Quaternion.of(RealScalar.of(-4), Tensors.vector(1, 2, 3));
    assertThrows(ClassCastException.class, () -> UnitStep.FUNCTION.apply(scalar));
  }

  @Test
  void testStringFail() {
    Scalar scalar = StringScalar.of("abc");
    assertThrows(Throw.class, () -> UnitStep.FUNCTION.apply(scalar));
  }
}
