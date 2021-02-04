// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

// Mathematica gives the result for 1/4, 1/3, 1/2 in exact precision
public class FlatTopWindowTest extends TestCase {
  public void testSimple() {
    ScalarUnaryOperator windowFunction = FlatTopWindow.FUNCTION;
    Scalar scalar = windowFunction.apply(RationalScalar.HALF);
    Chop._10.requireClose(scalar, Scalars.fromString("-210527/500000000"));
  }

  public void testQuarter() {
    ScalarUnaryOperator windowFunction = FlatTopWindow.FUNCTION;
    Scalar scalar = windowFunction.apply(RationalScalar.of(1, 4));
    Chop._10.requireClose(scalar, Scalars.fromString("-54736843/1000000000"));
  }

  public void testThird() {
    ScalarUnaryOperator windowFunction = FlatTopWindow.FUNCTION;
    Scalar scalar = windowFunction.apply(RationalScalar.of(1, 3));
    Chop._10.requireClose(scalar, Scalars.fromString("-51263159/1000000000"));
  }

  public void testTenth() {
    ScalarUnaryOperator windowFunction = FlatTopWindow.FUNCTION;
    Scalar scalar = windowFunction.apply(RationalScalar.of(1, 10));
    Chop._10.requireClose(scalar, Scalars.fromString("0.60687214957621189799"));
  }

  public void testOutside() {
    Scalar scalar = FlatTopWindow.FUNCTION.apply(RealScalar.of(-0.52));
    assertEquals(scalar, RealScalar.ZERO);
  }
}
