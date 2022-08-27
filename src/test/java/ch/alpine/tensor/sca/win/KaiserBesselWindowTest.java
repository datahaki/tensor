// code by jph
package ch.alpine.tensor.sca.win;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.mat.Tolerance;

class KaiserBesselWindowTest {
  @Test
  void testSimple() {
    Scalar scalar = KaiserBesselWindow.FUNCTION.apply(RealScalar.of(0.2));
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(0.47498876376122917));
  }

  @Test
  void testOutside() {
    Tolerance.CHOP.requireZero(KaiserBesselWindow.FUNCTION.apply(RealScalar.of(-0.51)));
    Tolerance.CHOP.requireZero(KaiserBesselWindow.FUNCTION.apply(RealScalar.of(+0.51)));
  }
}
