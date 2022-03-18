// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.usr.AssertFail;

public class ComplexScalarTest {
  @Test
  public void testSign() {
    Scalar scalar = ComplexScalar.of(4, 3);
    Scalar result = Sign.FUNCTION.apply(scalar);
    Tolerance.CHOP.requireClose(scalar, result.multiply(RealScalar.of(5)));
  }

  @Test
  public void testSignEps() {
    Scalar scalar = ComplexScalar.of(0, Double.MIN_VALUE);
    Scalar result = Sign.FUNCTION.apply(scalar);
    Tolerance.CHOP.requireClose(result, ComplexScalar.I);
  }

  @Test
  public void testSignEpsReIm() {
    Scalar scalar = ComplexScalar.of(Double.MIN_VALUE, Double.MIN_VALUE);
    Scalar result = Sign.FUNCTION.apply(scalar);
    Tolerance.CHOP.requireClose(result, ComplexScalar.of(0.7071067811865475, 0.7071067811865475));
  }

  @Test
  public void testSignEpsReImNeg() {
    Scalar scalar = ComplexScalar.of(Double.MIN_VALUE, -Double.MIN_VALUE);
    Scalar result = Sign.FUNCTION.apply(scalar);
    Tolerance.CHOP.requireClose(result, ComplexScalar.of(0.7071067811865475, -0.7071067811865475));
  }

  @Test
  public void testOne() {
    Scalar scalar = ComplexScalar.of(56, 217);
    assertEquals(scalar.one().multiply(scalar), scalar);
    assertEquals(scalar.multiply(scalar.one()), scalar);
  }

  @Test
  public void testConstructFail() {
    assertThrows(TensorRuntimeException.class, () -> ComplexScalar.of(RealScalar.ONE, ComplexScalar.I));
    assertThrows(TensorRuntimeException.class, () -> ComplexScalar.of(ComplexScalar.I, RealScalar.ONE));
  }

  @Test
  public void testNullFail() {
    assertThrows(TensorRuntimeException.class, () -> ComplexScalar.of(RealScalar.ONE, null));
    assertThrows(TensorRuntimeException.class, () -> ComplexScalar.of(null, RealScalar.ONE));
    assertThrows(TensorRuntimeException.class, () -> ComplexScalar.of(null, RealScalar.ZERO));
  }

  @Test
  public void testPolarFail() {
    AssertFail.of(() -> ComplexScalar.fromPolar(RealScalar.ONE, ComplexScalar.I));
    AssertFail.of(() -> ComplexScalar.fromPolar(ComplexScalar.I, RealScalar.ONE));
    AssertFail.of(() -> ComplexScalar.fromPolar(ComplexScalar.I, ComplexScalar.I));
  }

  @Test
  public void testPolarQuantityFail() {
    AssertFail.of(() -> ComplexScalar.fromPolar(RealScalar.ONE, Quantity.of(1.3, "m")));
  }

  @Test
  public void testPolar() {
    assertTrue(ComplexScalar.fromPolar(1, 3) instanceof ComplexScalar);
    assertTrue(ComplexScalar.fromPolar(1, 0) instanceof RealScalar);
  }

  @Test
  public void testPolarNumberFail() {
    AssertFail.of(() -> ComplexScalar.fromPolar(-1, 3));
  }

  @Test
  public void testUnitExp() {
    Scalar theta = RealScalar.of(0.3);
    Tolerance.CHOP.requireClose( //
        Exp.FUNCTION.apply(theta.multiply(ComplexScalar.I)), //
        ComplexScalar.unit(theta));
  }

  @Test
  public void testUnitFail() {
    AssertFail.of(() -> ComplexScalar.unit(ComplexScalar.of(-1, 3)));
    AssertFail.of(() -> ComplexScalar.unit(Quantity.of(3, "s")));
  }
}
