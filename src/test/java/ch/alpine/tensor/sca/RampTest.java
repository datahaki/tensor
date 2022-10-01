// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;

class RampTest {
  @Test
  void testRealScalar() {
    assertEquals(Ramp.of(RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(Ramp.FUNCTION.apply(RealScalar.of(-6)), RealScalar.ZERO);
    assertEquals(Ramp.of(RealScalar.of(26)), RealScalar.of(26));
  }

  @Test
  void testInfty() {
    assertEquals(Ramp.of(DoubleScalar.POSITIVE_INFINITY), DoubleScalar.POSITIVE_INFINITY);
  }

  @Test
  void testQuantity() {
    Scalar qs1 = Quantity.of(1, "m");
    Scalar qs2 = Quantity.of(-2, "m");
    assertEquals(Ramp.of(qs1), qs1);
    assertEquals(Ramp.of(qs2), qs1.zero());
  }

  @Test
  void testGaussScalar() {
    for (int index = -10; index < 15; ++index) {
      Scalar qs1 = GaussScalar.of(index, 13);
      assertEquals(Ramp.of(qs1), qs1);
    }
  }
}
