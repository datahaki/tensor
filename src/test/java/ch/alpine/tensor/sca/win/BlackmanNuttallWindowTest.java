// code by jph
package ch.alpine.tensor.sca.win;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.Tolerance;

// Mathematica gives the result for 1/4, 1/3, 1/2 in exact precision
class BlackmanNuttallWindowTest {
  @Test
  void testSimple() {
    ScalarUnaryOperator windowFunction = BlackmanNuttallWindow.FUNCTION;
    Scalar scalar = windowFunction.apply(Rational.HALF);
    Tolerance.CHOP.requireClose(scalar, Scalars.fromString("907/2500000"));
  }

  @Test
  void testQuarter() {
    ScalarUnaryOperator windowFunction = BlackmanNuttallWindow.FUNCTION;
    Scalar scalar = windowFunction.apply(Rational.of(1, 4));
    Tolerance.CHOP.requireClose(scalar, Scalars.fromString("17733/78125"));
  }

  @Test
  void testThird() {
    ScalarUnaryOperator windowFunction = BlackmanNuttallWindow.FUNCTION;
    Scalar scalar = windowFunction.apply(Rational.of(1, 3));
    Tolerance.CHOP.requireClose(scalar, Scalars.fromString("122669/2000000"));
  }

  @Test
  void testOutside() {
    Scalar scalar = BlackmanNuttallWindow.FUNCTION.apply(RealScalar.of(-0.52));
    assertEquals(scalar, RealScalar.ZERO);
  }
}
