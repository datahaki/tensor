// code by jph
package ch.alpine.tensor.sca.win;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.sca.Chop;

class BartlettWindowTest {
  @Test
  void testZero() {
    Scalar scalar = BartlettWindow.FUNCTION.apply(RealScalar.ZERO);
    assertEquals(scalar, RealScalar.ONE);
  }

  @Test
  void testExact() {
    Scalar scalar = BartlettWindow.FUNCTION.apply(Rational.of(3, 3465));
    ExactScalarQ.require(scalar);
    assertEquals(scalar, Rational.of(1153, 1155));
  }

  @Test
  void testExact2() {
    Scalar scalar = BartlettWindow.FUNCTION.apply(Rational.of(2, 5));
    ExactScalarQ.require(scalar);
    assertFalse(Scalars.isZero(scalar));
  }

  @Test
  void testContinuous() {
    Scalar scalar = BartlettWindow.FUNCTION.apply(RealScalar.of(0.499999999));
    Chop._07.requireZero(scalar);
  }

  @Test
  void testSemiExact() {
    Scalar scalar = BartlettWindow.FUNCTION.apply(Rational.HALF);
    assertTrue(Scalars.isZero(scalar));
    ExactScalarQ.require(scalar);
  }

  @Test
  void testOutside() {
    Scalar scalar = BartlettWindow.FUNCTION.apply(RealScalar.of(-0.52));
    assertEquals(scalar, RealScalar.ZERO);
  }
}
