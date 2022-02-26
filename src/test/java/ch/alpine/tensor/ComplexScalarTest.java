// code by jph
package ch.alpine.tensor;

import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Exp;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ComplexScalarTest extends TestCase {
  public void testSign() {
    Scalar scalar = ComplexScalar.of(4, 3);
    Scalar result = Sign.FUNCTION.apply(scalar);
    Tolerance.CHOP.requireClose(scalar, result.multiply(RealScalar.of(5)));
  }

  public void testSignEps() {
    Scalar scalar = ComplexScalar.of(0, Double.MIN_VALUE);
    Scalar result = Sign.FUNCTION.apply(scalar);
    Tolerance.CHOP.requireClose(result, ComplexScalar.I);
  }

  public void testSignEpsReIm() {
    Scalar scalar = ComplexScalar.of(Double.MIN_VALUE, Double.MIN_VALUE);
    Scalar result = Sign.FUNCTION.apply(scalar);
    Tolerance.CHOP.requireClose(result, ComplexScalar.of(0.7071067811865475, 0.7071067811865475));
  }

  public void testSignEpsReImNeg() {
    Scalar scalar = ComplexScalar.of(Double.MIN_VALUE, -Double.MIN_VALUE);
    Scalar result = Sign.FUNCTION.apply(scalar);
    Tolerance.CHOP.requireClose(result, ComplexScalar.of(0.7071067811865475, -0.7071067811865475));
  }

  public void testOne() {
    Scalar scalar = ComplexScalar.of(56, 217);
    assertEquals(scalar.one().multiply(scalar), scalar);
    assertEquals(scalar.multiply(scalar.one()), scalar);
  }

  public void testConstructFail() {
    AssertFail.of(() -> ComplexScalar.of(RealScalar.ONE, ComplexScalar.I));
    AssertFail.of(() -> ComplexScalar.of(ComplexScalar.I, RealScalar.ONE));
  }

  public void testNullFail() {
    AssertFail.of(() -> ComplexScalar.of(RealScalar.ONE, null));
    AssertFail.of(() -> ComplexScalar.of(null, RealScalar.ONE));
    AssertFail.of(() -> ComplexScalar.of(null, RealScalar.ZERO));
  }

  public void testPolarFail() {
    AssertFail.of(() -> ComplexScalar.fromPolar(RealScalar.ONE, ComplexScalar.I));
    AssertFail.of(() -> ComplexScalar.fromPolar(ComplexScalar.I, RealScalar.ONE));
    AssertFail.of(() -> ComplexScalar.fromPolar(ComplexScalar.I, ComplexScalar.I));
  }

  public void testPolarQuantityFail() {
    AssertFail.of(() -> ComplexScalar.fromPolar(RealScalar.ONE, Quantity.of(1.3, "m")));
  }

  public void testPolar() {
    assertTrue(ComplexScalar.fromPolar(1, 3) instanceof ComplexScalar);
    assertTrue(ComplexScalar.fromPolar(1, 0) instanceof RealScalar);
  }

  public void testPolarNumberFail() {
    AssertFail.of(() -> ComplexScalar.fromPolar(-1, 3));
  }

  public void testUnitExp() {
    Scalar theta = RealScalar.of(0.3);
    Tolerance.CHOP.requireClose( //
        Exp.FUNCTION.apply(theta.multiply(ComplexScalar.I)), //
        ComplexScalar.unit(theta));
  }

  public void testUnitFail() {
    AssertFail.of(() -> ComplexScalar.unit(ComplexScalar.of(-1, 3)));
    AssertFail.of(() -> ComplexScalar.unit(Quantity.of(3, "s")));
  }
}
