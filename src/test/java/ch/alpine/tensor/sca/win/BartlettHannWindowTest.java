// code by jph
package ch.alpine.tensor.sca.win;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.mat.Tolerance;

class BartlettHannWindowTest {
  @Test
  void testSimple() {
    Tolerance.CHOP.requireClose( //
        BartlettHannWindow.FUNCTION.apply(RealScalar.of(0.3)), //
        RealScalar.of(0.35857354213752));
    Tolerance.CHOP.requireClose( //
        BartlettHannWindow.FUNCTION.apply(RealScalar.of(0.5)), //
        RealScalar.of(0));
  }
}
