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
public class BlackmanHarrisWindowTest extends TestCase {
  public void testSimple() {
    ScalarUnaryOperator windowFunction = BlackmanHarrisWindow.FUNCTION;
    Scalar scalar = windowFunction.apply(RationalScalar.HALF);
    Tolerance.CHOP.requireClose(scalar, Scalars.fromString("3/50000"));
  }

  public void testQuarter() {
    ScalarUnaryOperator windowFunction = BlackmanHarrisWindow.FUNCTION;
    Scalar scalar = windowFunction.apply(RationalScalar.of(1, 4));
    Tolerance.CHOP.requireClose(scalar, RationalScalar.of(21747, 100000));
  }

  public void testThird() {
    ScalarUnaryOperator windowFunction = BlackmanHarrisWindow.FUNCTION;
    Scalar scalar = windowFunction.apply(RationalScalar.of(1, 3));
    Tolerance.CHOP.requireClose(scalar, RationalScalar.of(11129, 200000));
  }

  public void testOutside() {
    Scalar scalar = BlackmanHarrisWindow.FUNCTION.apply(RealScalar.of(-0.52));
    assertEquals(scalar, RealScalar.ZERO);
  }
}
