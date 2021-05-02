// code by jph
package ch.alpine.tensor.sca.win;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class CauchyWindowTest extends TestCase {
  public void testSimple() {
    ScalarUnaryOperator suo = CauchyWindow.of(RealScalar.of(1.6));
    Tolerance.CHOP.requireClose( //
        suo.apply(RealScalar.of(0.4)), //
        RealScalar.of(0.3790175864160096));
    Tolerance.CHOP.requireClose( //
        suo.apply(RealScalar.of(0.5)), //
        RealScalar.of(0.28089887640449435));
  }

  public void testExact() {
    Scalar scalar = CauchyWindow.of(RationalScalar.of(7, 8)).apply(RationalScalar.of(2, 5));
    ExactScalarQ.require(scalar);
    assertFalse(Scalars.isZero(scalar));
  }

  public void testNullFail() {
    AssertFail.of(() -> CauchyWindow.of(null));
  }
}
