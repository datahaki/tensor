// code by jph
package ch.alpine.tensor.sca.win;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class HannPoissonWindowTest extends TestCase {
  public void testSimple() {
    ScalarUnaryOperator suo = HannPoissonWindow.of(RealScalar.of(1.3));
    Tolerance.CHOP.requireClose( //
        suo.apply(RealScalar.of(0.4)), //
        RealScalar.of(0.03375191875636745));
    Tolerance.CHOP.requireClose( //
        suo.apply(RealScalar.of(0.5)), //
        RealScalar.of(0));
  }

  public void testNullFail() {
    AssertFail.of(() -> HannPoissonWindow.of(null));
  }
}
