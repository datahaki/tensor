// code by jph
package ch.alpine.tensor.sca.win;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.usr.AssertFail;

public class HannPoissonWindowTest {
  @Test
  public void testSimple() {
    ScalarUnaryOperator suo = HannPoissonWindow.of(RealScalar.of(1.3));
    Tolerance.CHOP.requireClose( //
        suo.apply(RealScalar.of(0.4)), //
        RealScalar.of(0.03375191875636745));
    Tolerance.CHOP.requireClose( //
        suo.apply(RealScalar.of(0.5)), //
        RealScalar.of(0));
  }

  @Test
  public void testNullFail() {
    AssertFail.of(() -> HannPoissonWindow.of(null));
  }
}
