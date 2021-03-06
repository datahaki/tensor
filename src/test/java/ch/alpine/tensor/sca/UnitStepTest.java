// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.lie.Quaternion;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class UnitStepTest extends TestCase {
  public void testRealScalar() {
    assertEquals(UnitStep.of(RealScalar.of(-0.3)), RealScalar.ZERO);
    assertEquals(UnitStep.of(RealScalar.of(0)), RealScalar.ONE);
    assertEquals(UnitStep.of(RealScalar.of(0.134)), RealScalar.ONE);
  }

  public void testPredicateQuantity() {
    assertEquals(UnitStep.of(Quantity.of(-0.3, "m")), RealScalar.ZERO);
    assertEquals(UnitStep.of(Quantity.of(0.0, "m")), RealScalar.ONE);
    assertEquals(UnitStep.of(Quantity.of(0, "m")), RealScalar.ONE);
    assertEquals(UnitStep.of(Quantity.of(1, "m")), RealScalar.ONE);
  }

  public void testGaussScalar() {
    assertEquals(UnitStep.FUNCTION.apply(GaussScalar.of(2, 7)), RealScalar.ONE);
  }

  public void testQuaternionFail() {
    Scalar scalar = Quaternion.of(RealScalar.of(-4), Tensors.vector(1, 2, 3));
    AssertFail.of(() -> UnitStep.FUNCTION.apply(scalar));
  }

  public void testStringFail() {
    Scalar scalar = StringScalar.of("abc");
    AssertFail.of(() -> UnitStep.FUNCTION.apply(scalar));
  }
}
