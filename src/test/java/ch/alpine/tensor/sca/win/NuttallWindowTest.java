// code by jph
package ch.alpine.tensor.sca.win;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;

public class NuttallWindowTest {
  @Test
  public void testZero() {
    ScalarUnaryOperator scalarUnaryOperator = NuttallWindow.FUNCTION;
    assertEquals(scalarUnaryOperator.apply(RealScalar.ZERO), RealScalar.ONE);
  }

  @Test
  public void testOutside() {
    assertEquals(NuttallWindow.FUNCTION.apply(RealScalar.of(-0.52)), RealScalar.ZERO);
  }

  @Test
  public void testQuantityFail() {
    AssertFail.of(() -> NuttallWindow.FUNCTION.apply(Quantity.of(0, "s")));
    AssertFail.of(() -> NuttallWindow.FUNCTION.apply(Quantity.of(2, "s")));
  }
}
