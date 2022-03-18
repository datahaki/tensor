// code by jph
package ch.alpine.tensor.sca.win;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.pow.Sqrt;

public class LanczosWindowTest {
  @Test
  public void testSimple() {
    Chop._15.requireClose(LanczosWindow.FUNCTION.apply(RealScalar.of(0.125)), Sqrt.of(RealScalar.of(2)).divide(Pi.HALF));
    Chop._15.requireClose(LanczosWindow.FUNCTION.apply(RealScalar.of(0.25)), Pi.HALF.reciprocal());
    Chop._15.requireClose(LanczosWindow.FUNCTION.apply(RealScalar.of(0.5)), RealScalar.ZERO);
    assertTrue(Scalars.isZero(LanczosWindow.FUNCTION.apply(RealScalar.of(0.51))));
  }
}
