// code by jph
package ch.alpine.tensor.sca.win;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.mat.Tolerance;

public class CauchyWindowTest {
  @Test
  public void testSimple() {
    ScalarUnaryOperator suo = CauchyWindow.of(RealScalar.of(1.6));
    Tolerance.CHOP.requireClose( //
        suo.apply(RealScalar.of(0.4)), //
        RealScalar.of(0.3790175864160096));
    Tolerance.CHOP.requireClose( //
        suo.apply(RealScalar.of(0.5)), //
        RealScalar.of(0.28089887640449435));
  }

  @Test
  public void testExact() {
    Scalar scalar = CauchyWindow.of(RationalScalar.of(7, 8)).apply(RationalScalar.of(2, 5));
    ExactScalarQ.require(scalar);
    assertFalse(Scalars.isZero(scalar));
  }

  @Test
  public void testNullFail() {
    assertThrows(NullPointerException.class, () -> CauchyWindow.of(null));
  }
}
