// code by jph
package ch.alpine.tensor.sca.win;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.Tolerance;

public class CosineWindowTest {
  @Test
  public void testSimple() {
    ScalarUnaryOperator connesWindow = CosineWindow.of(RealScalar.of(1.6));
    Tolerance.CHOP.requireClose( //
        connesWindow.apply(RealScalar.of(0.4)), //
        RealScalar.of(0.15274668107476286));
    Tolerance.CHOP.requireZero( //
        connesWindow.apply(RealScalar.of(0.5)));
  }

  @Test
  public void testNullFail() {
    assertThrows(NullPointerException.class, () -> CosineWindow.of(null));
  }
}
