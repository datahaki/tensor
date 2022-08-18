// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.lie.Quaternion;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Exp;

class ComplexScalarTest {
  @Test
  void testSign() {
    Scalar scalar = ComplexScalar.of(4, 3);
    Scalar result = Sign.FUNCTION.apply(scalar);
    Tolerance.CHOP.requireClose(scalar, result.multiply(RealScalar.of(5)));
  }

  @Test
  void testSignEps() {
    Scalar scalar = ComplexScalar.of(0, Double.MIN_VALUE);
    Scalar result = Sign.FUNCTION.apply(scalar);
    Tolerance.CHOP.requireClose(result, ComplexScalar.I);
  }

  @Test
  void testSignEpsReIm() {
    Scalar scalar = ComplexScalar.of(Double.MIN_VALUE, Double.MIN_VALUE);
    Scalar result = Sign.FUNCTION.apply(scalar);
    Tolerance.CHOP.requireClose(result, ComplexScalar.of(0.7071067811865475, 0.7071067811865475));
  }

  @Test
  void testSignEpsReImNeg() {
    Scalar scalar = ComplexScalar.of(Double.MIN_VALUE, -Double.MIN_VALUE);
    Scalar result = Sign.FUNCTION.apply(scalar);
    Tolerance.CHOP.requireClose(result, ComplexScalar.of(0.7071067811865475, -0.7071067811865475));
  }

  @Test
  void testOne() {
    Scalar scalar = ComplexScalar.of(56, 217);
    assertEquals(scalar.one().multiply(scalar), scalar);
    assertEquals(scalar.multiply(scalar.one()), scalar);
  }

  @Test
  void testConstructFail() {
    assertThrows(Throw.class, () -> ComplexScalar.of(RealScalar.ONE, ComplexScalar.I));
    assertThrows(Throw.class, () -> ComplexScalar.of(ComplexScalar.I, RealScalar.ONE));
    assertThrows(Throw.class, () -> ComplexScalar.of(Quaternion.ONE, RealScalar.ONE));
    assertThrows(Throw.class, () -> ComplexScalar.of(RealScalar.ONE, Quaternion.ONE));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> ComplexScalar.of(RealScalar.ONE, null));
    assertThrows(NullPointerException.class, () -> ComplexScalar.of(null, RealScalar.ONE));
    assertThrows(NullPointerException.class, () -> ComplexScalar.of(null, RealScalar.ZERO));
  }

  @Test
  void testPolarFail() {
    assertThrows(Throw.class, () -> ComplexScalar.fromPolar(RealScalar.ONE, ComplexScalar.I));
    assertThrows(ClassCastException.class, () -> ComplexScalar.fromPolar(ComplexScalar.I, RealScalar.ONE));
    assertThrows(ClassCastException.class, () -> ComplexScalar.fromPolar(ComplexScalar.I, ComplexScalar.I));
  }

  @Test
  void testPolarQuantityFail() {
    assertThrows(Throw.class, () -> ComplexScalar.fromPolar(RealScalar.ONE, Quantity.of(1.3, "m")));
  }

  @Test
  void testPolar() {
    assertInstanceOf(ComplexScalar.class, ComplexScalar.fromPolar(1, 3));
    assertInstanceOf(RealScalar.class, ComplexScalar.fromPolar(1, 0));
  }

  @Test
  void testPolarNumberFail() {
    assertThrows(Throw.class, () -> ComplexScalar.fromPolar(-1, 3));
  }

  @Test
  void testUnitExp() {
    Scalar theta = RealScalar.of(0.3);
    Tolerance.CHOP.requireClose( //
        Exp.FUNCTION.apply(theta.multiply(ComplexScalar.I)), //
        ComplexScalar.unit(theta));
  }

  @Test
  void testGaussScalar() {
    Scalar scalar = ComplexScalar.of(GaussScalar.of(3, 7), GaussScalar.of(2, 7));
    Scalar invers = scalar.reciprocal();
    assertEquals(scalar.multiply(invers), GaussScalar.of(1, 7));
    assertEquals(invers.multiply(scalar), GaussScalar.of(1, 7));
  }

  @Test
  void testGaussScalarCommute() {
    int p = 43;
    Scalar cs = ComplexScalar.of(GaussScalar.of(31, p), GaussScalar.of(22, p));
    Scalar gs = GaussScalar.of(16, p);
    assertEquals(cs.multiply(gs), gs.multiply(cs));
    assertEquals(cs.divide(gs), gs.under(cs));
    assertEquals(cs.under(gs), gs.divide(cs));
  }

  @Test
  void testAsField() {
    // primes not resulting in field: 5,13,17
    // primes resulting in field:
    for (int p : new int[] { 3, 7, 11, 19 }) { // also 23, 31, 43
      Scalar neutral = GaussScalar.of(1, p);
      for (int i = 0; i < p; ++i)
        for (int j = (0 < i ? 0 : 1); j < p; ++j) {
          Scalar scalar = ComplexScalar.of(GaussScalar.of(i, p), GaussScalar.of(j, p));
          assertEquals(scalar.reciprocal().multiply(scalar), neutral);
          assertEquals(scalar.multiply(scalar.reciprocal()), neutral);
          assertEquals(scalar.divide(scalar), neutral);
          assertEquals(scalar.under(scalar), neutral);
        }
    }
  }

  @Test
  void testUnitFail() {
    assertThrows(Throw.class, () -> ComplexScalar.unit(ComplexScalar.of(-1, 3)));
    assertThrows(Throw.class, () -> ComplexScalar.unit(Quantity.of(3, "s")));
  }
}
