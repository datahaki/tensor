// code by jph
package ch.alpine.tensor.sca.win;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.Tolerance;
import junit.framework.TestCase;

// Mathematica gives the result for 1/4, 1/3, 1/2 in exact precision
public class FlatTopWindowTest extends TestCase {
  public void testSimple() {
    ScalarUnaryOperator windowFunction = FlatTopWindow.FUNCTION;
    Scalar scalar = windowFunction.apply(RationalScalar.HALF);
    Tolerance.CHOP.requireClose(scalar, Scalars.fromString("-210527/500000000"));
  }

  public void testQuarter() {
    ScalarUnaryOperator windowFunction = FlatTopWindow.FUNCTION;
    Scalar scalar = windowFunction.apply(RationalScalar.of(1, 4));
    Tolerance.CHOP.requireClose(scalar, Scalars.fromString("-54736843/1000000000"));
  }

  public void testThird() {
    ScalarUnaryOperator windowFunction = FlatTopWindow.FUNCTION;
    Scalar scalar = windowFunction.apply(RationalScalar.of(1, 3));
    Tolerance.CHOP.requireClose(scalar, Scalars.fromString("-51263159/1000000000"));
  }

  public void testTenth() {
    ScalarUnaryOperator windowFunction = FlatTopWindow.FUNCTION;
    Scalar scalar = windowFunction.apply(RationalScalar.of(1, 10));
    Tolerance.CHOP.requireClose(scalar, Scalars.fromString("0.60687214957621189799"));
  }

  public void testOutside() {
    Scalar scalar = FlatTopWindow.FUNCTION.apply(RealScalar.of(-0.52));
    assertEquals(scalar, RealScalar.ZERO);
  }
}
