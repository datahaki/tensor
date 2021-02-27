// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class NuttallWindowTest extends TestCase {
  public void testZero() {
    ScalarUnaryOperator scalarUnaryOperator = NuttallWindow.FUNCTION;
    assertEquals(scalarUnaryOperator.apply(RealScalar.ZERO), RealScalar.ONE);
  }

  public void testOutside() {
    assertEquals(NuttallWindow.FUNCTION.apply(RealScalar.of(-0.52)), RealScalar.ZERO);
  }

  public void testQuantityFail() {
    AssertFail.of(() -> NuttallWindow.FUNCTION.apply(Quantity.of(0, "s")));
    AssertFail.of(() -> NuttallWindow.FUNCTION.apply(Quantity.of(2, "s")));
  }
}
