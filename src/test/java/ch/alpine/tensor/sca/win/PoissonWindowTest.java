// code by jph
package ch.alpine.tensor.sca.win;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.Tolerance;

class PoissonWindowTest {
  @Test
  void testSimple() {
    ScalarUnaryOperator suo = PoissonWindow.of(RealScalar.of(1.6));
    Tolerance.CHOP.requireClose( //
        suo.apply(RealScalar.of(0.4)), //
        RealScalar.of(0.2780373004531941));
    Tolerance.CHOP.requireClose( //
        suo.apply(RealScalar.of(0.5)), //
        RealScalar.of(0.20189651799465538));
  }
}
