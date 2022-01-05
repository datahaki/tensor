// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Around;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class RampTest extends TestCase {
  public void testRealScalar() {
    assertEquals(Ramp.of(RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(Ramp.FUNCTION.apply(RealScalar.of(-6)), RealScalar.ZERO);
    assertEquals(Ramp.of(RealScalar.of(26)), RealScalar.of(26));
  }

  public void testInfty() {
    assertEquals(Ramp.of(DoubleScalar.POSITIVE_INFINITY), DoubleScalar.POSITIVE_INFINITY);
  }

  public void testQuantity() {
    Scalar qs1 = Quantity.of(1, "m");
    Scalar qs2 = Quantity.of(-2, "m");
    assertEquals(Ramp.of(qs1), qs1);
    assertEquals(Ramp.of(qs2), qs1.zero());
  }

  public void testGaussScalar() {
    for (int index = -10; index < 15; ++index) {
      Scalar qs1 = GaussScalar.of(index, 13);
      assertEquals(Ramp.of(qs1), qs1);
    }
  }

  public void testFail() {
    Scalar scalar = Around.of(2, 3);
    AssertFail.of(() -> Ramp.FUNCTION.apply(scalar));
  }
}
