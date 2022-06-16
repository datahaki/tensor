// code by jph
package ch.alpine.tensor.sca.win;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.Tolerance;

// Mathematica gives the result for 1/4, 1/3, 1/2 in exact precision
class FlatTopWindowTest {
  @Test
  void testSimple() {
    ScalarUnaryOperator windowFunction = FlatTopWindow.FUNCTION;
    Scalar scalar = windowFunction.apply(RationalScalar.HALF);
    Tolerance.CHOP.requireClose(scalar, Scalars.fromString("-210527/500000000"));
  }

  @Test
  void testQuarter() {
    ScalarUnaryOperator windowFunction = FlatTopWindow.FUNCTION;
    Scalar scalar = windowFunction.apply(RationalScalar.of(1, 4));
    Tolerance.CHOP.requireClose(scalar, Scalars.fromString("-54736843/1000000000"));
  }

  @Test
  void testThird() {
    ScalarUnaryOperator windowFunction = FlatTopWindow.FUNCTION;
    Scalar scalar = windowFunction.apply(RationalScalar.of(1, 3));
    Tolerance.CHOP.requireClose(scalar, Scalars.fromString("-51263159/1000000000"));
  }

  @Test
  void testTenth() {
    ScalarUnaryOperator windowFunction = FlatTopWindow.FUNCTION;
    Scalar scalar = windowFunction.apply(RationalScalar.of(1, 10));
    Tolerance.CHOP.requireClose(scalar, Scalars.fromString("0.60687214957621189799"));
  }

  @Test
  void testOutside() {
    Scalar scalar = FlatTopWindow.FUNCTION.apply(RealScalar.of(-0.52));
    assertEquals(scalar, RealScalar.ZERO);
  }
}
