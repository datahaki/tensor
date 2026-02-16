// code by jph
package ch.alpine.tensor.sca.win;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.mat.Tolerance;

class ConnesWindowTest {
  @Test
  void testSimple() {
    ScalarUnaryOperator connesWindow = ConnesWindow.of(RealScalar.of(1.6));
    Tolerance.CHOP.requireClose( //
        connesWindow.apply(RealScalar.of(0.4)), //
        RealScalar.of(0.5625));
    Tolerance.CHOP.requireClose( //
        connesWindow.apply(RealScalar.of(0.5)), //
        RealScalar.of(0.3713378906250001));
  }

  @Test
  void testExact() {
    Scalar scalar = ConnesWindow.of(Rational.of(7, 8)).apply(Rational.of(2, 5));
    ExactScalarQ.require(scalar);
    assertFalse(Scalars.isZero(scalar));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> ConnesWindow.of(null));
  }
}
