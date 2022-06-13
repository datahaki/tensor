// code by jph
package ch.alpine.tensor.sca.win;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.Tolerance;

class HannPoissonWindowTest {
  @Test
  void testSimple() {
    ScalarUnaryOperator suo = HannPoissonWindow.of(RealScalar.of(1.3));
    Tolerance.CHOP.requireClose( //
        suo.apply(RealScalar.of(0.4)), //
        RealScalar.of(0.03375191875636745));
    Tolerance.CHOP.requireClose( //
        suo.apply(RealScalar.of(0.5)), //
        RealScalar.of(0));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> HannPoissonWindow.of(null));
  }
}
