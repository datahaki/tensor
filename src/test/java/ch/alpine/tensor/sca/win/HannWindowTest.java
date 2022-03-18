// code by jph
package ch.alpine.tensor.sca.win;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;

public class HannWindowTest {
  @Test
  public void testSimple() {
    ScalarUnaryOperator suo = HannWindow.of(RealScalar.of(0.8));
    Tolerance.CHOP.requireClose( //
        suo.apply(RealScalar.of(0.4)), //
        RealScalar.of(0.6381966011250106));
    Tolerance.CHOP.requireClose( //
        suo.apply(RealScalar.of(0.5)), //
        RealScalar.of(0.6));
  }

  @Test
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

  @Test
  public void testExactFallback() {
    ScalarUnaryOperator scalarUnaryOperator = HannWindow.FUNCTION;
    Scalar scalar = scalarUnaryOperator.apply(RationalScalar.of(1, 7));
    assertEquals(scalar, RealScalar.of(0.8117449009293667));
  }

  @Test
  public void testZero() {
    ScalarUnaryOperator scalarUnaryOperator = HannWindow.FUNCTION;
    assertEquals(scalarUnaryOperator.apply(RationalScalar.of(7, 12)), RealScalar.ZERO);
  }

  @Test
  public void testNumeric() {
    ScalarUnaryOperator scalarUnaryOperator = HannWindow.FUNCTION;
    assertEquals(scalarUnaryOperator.apply(RealScalar.of(0.25)), RationalScalar.HALF);
  }

  @Test
  public void testSemiExact() {
    Scalar scalar = HannWindow.FUNCTION.apply(RationalScalar.HALF);
    assertTrue(Scalars.isZero(scalar));
  }

  @Test
  public void testToString() {
    assertEquals(HannWindow.FUNCTION.toString(), "HannWindow[1/2]");
  }

  @Test
  public void testQuantityFail() {
    AssertFail.of(() -> HannWindow.FUNCTION.apply(Quantity.of(2, "s")));
  }

  @Test
  public void testNullFail() {
    AssertFail.of(() -> HannWindow.of(null));
  }
}