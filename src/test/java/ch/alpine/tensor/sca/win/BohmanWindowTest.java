// code by jph
package ch.alpine.tensor.sca.win;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.mat.Tolerance;

class BohmanWindowTest {
  @Test
  public void testSimple() {
    Tolerance.CHOP.requireClose(BohmanWindow.FUNCTION.apply(RealScalar.of(0.3)), RealScalar.of(0.1791238937062839));
  }
}
