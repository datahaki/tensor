// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class HannWindowTest extends TestCase {
  public void testSimple() {
    ScalarUnaryOperator suo = HannWindow.of(RealScalar.of(0.8));
    Tolerance.CHOP.requireClose( //
        suo.apply(RealScalar.of(0.4)), //
        RealScalar.of(0.6381966011250106));
    Tolerance.CHOP.requireClose( //
        suo.apply(RealScalar.of(0.5)), //
        RealScalar.of(0.6));
  }

  public void testExact() {
    ScalarUnaryOperator scalarUnaryOperator = HannWindow.FUNCTION;
    assertEquals(scalarUnaryOperator.apply(RealScalar.ZERO), RealScalar.ONE);
    Tolerance.CHOP.requireClose(scalarUnaryOperator.apply(RationalScalar.of(+1, 3)), RationalScalar.of(1, 4));
    Tolerance.CHOP.requireClose(scalarUnaryOperator.apply(RationalScalar.of(+1, 4)), RationalScalar.of(1, 2));
    Tolerance.CHOP.requireClose(scalarUnaryOperator.apply(RationalScalar.of(+1, 6)), RationalScalar.of(3, 4));
    Tolerance.CHOP.requireClose(scalarUnaryOperator.apply(RationalScalar.of(-1, 3)), RationalScalar.of(1, 4));
    Tolerance.CHOP.requireClose(scalarUnaryOperator.apply(RationalScalar.of(-1, 4)), RationalScalar.of(1, 2));
    Tolerance.CHOP.requireClose(scalarUnaryOperator.apply(RationalScalar.of(-1, 6)), RationalScalar.of(3, 4));
  }

  public void testExactFallback() {
    ScalarUnaryOperator scalarUnaryOperator = HannWindow.FUNCTION;
    Scalar scalar = scalarUnaryOperator.apply(RationalScalar.of(1, 7));
    assertEquals(scalar, RealScalar.of(0.8117449009293667));
  }

  public void testZero() {
    ScalarUnaryOperator scalarUnaryOperator = HannWindow.FUNCTION;
    assertEquals(scalarUnaryOperator.apply(RationalScalar.of(7, 12)), RealScalar.ZERO);
  }

  public void testNumeric() {
    ScalarUnaryOperator scalarUnaryOperator = HannWindow.FUNCTION;
    assertEquals(scalarUnaryOperator.apply(RealScalar.of(0.25)), RationalScalar.HALF);
  }

  public void testSemiExact() {
    Scalar scalar = HannWindow.FUNCTION.apply(RationalScalar.HALF);
    assertTrue(Scalars.isZero(scalar));
  }

  public void testQuantityFail() {
    AssertFail.of(() -> HannWindow.FUNCTION.apply(Quantity.of(2, "s")));
  }
}
