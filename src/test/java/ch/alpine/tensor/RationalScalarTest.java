// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.alg.Sort;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.pow.Power;

class RationalScalarTest {
  @Test
  void testAdd() {
    Scalar a = RationalScalar.of(3, 17);
    Scalar b = RationalScalar.of(4, 21);
    Scalar c = RationalScalar.of(131, 357);
    assertEquals(a.add(b), c);
    Tensor d = Tensors.of(a, b);
    assertEquals(Total.of(d), c);
    Tensor v = Tensors.of(DoubleScalar.of(2), RationalScalar.of(3, 2));
    Scalar s = RationalScalar.of(3 + 2 * 2, 2);
    assertEquals(Total.of(v), DoubleScalar.of(s.number().doubleValue()));
  }

  @Test
  void testReciprocal() {
    Scalar a = RationalScalar.of(3, -17);
    assertEquals(a.reciprocal(), RationalScalar.of(-17, 3));
  }

  @Test
  void testInvertFail() {
    assertThrows(ArithmeticException.class, () -> RationalScalar.of(0, 1).reciprocal());
  }

  @Test
  void testNegate() {
    Scalar scalar = RationalScalar.of(3, 17).negate();
    assertEquals(scalar, RationalScalar.of(3, -17));
    assertEquals(scalar.toString(), "-3/17");
  }

  @Test
  void testTensor() {
    Tensor a = Tensors.of( //
        RationalScalar.of(1, 2), //
        RationalScalar.of(3, 5), //
        RationalScalar.of(2, 7) //
    );
    Tensor b = Tensors.of( //
        RationalScalar.of(5, 13), //
        RationalScalar.of(0, 5), //
        RationalScalar.of(-3, 2) //
    );
    assertEquals(a.dot(b), RationalScalar.of(-43, 182));
  }

  @Test
  void testMultiply() {
    Tensor a = Tensors.of( //
        RationalScalar.of(1, 2), //
        RationalScalar.of(3, 5), //
        RationalScalar.of(2, 7) //
    );
    assertEquals(a.add(a), a.multiply(RationalScalar.of(2, 1)));
  }

  @Test
  void testSolve() {
    Tensor a1 = Tensors.of( //
        RationalScalar.of(1, 2), //
        RationalScalar.of(3, 5) //
    );
    Tensor a2 = Tensors.of( //
        RationalScalar.of(5, 2), //
        RationalScalar.of(19, 5) //
    );
    Tensor b = Tensors.of( //
        RationalScalar.of(8, 9), //
        RationalScalar.of(-3, 11) //
    );
    Tensor A = Tensors.of(a1, a2);
    Tensor sol = LinearSolve.of(A, b);
    Tensor x = Tensors.of( //
        RationalScalar.of(1753, 198), //
        RationalScalar.of(-2335, 396) //
    );
    assertEquals(sol, x);
  }

  @Test
  void testDouble1over3() {
    Scalar r = RationalScalar.of(1, 3);
    double d = r.number().doubleValue();
    double e = 1.0 / 3.0;
    assertEquals(d, e);
  }

  @Test
  void testDouble2over3() {
    Scalar r = RationalScalar.of(2, 3);
    double d = r.number().doubleValue();
    double e = Math.nextUp(2.0 / 3.0);
    assertEquals(d, e);
  }

  @Test
  void testPower() {
    assertEquals(Power.of(RationalScalar.of(2, 3), 0), RealScalar.ONE);
    assertEquals(Power.of(RationalScalar.of(2, 3), 1), RationalScalar.of(2, 3));
    assertEquals(Power.of(RationalScalar.of(2, 3), 3), RationalScalar.of(2 * 2 * 2, 3 * 3 * 3));
    assertEquals(Power.of(RationalScalar.of(2, 3), -3), RationalScalar.of(3 * 3 * 3, 2 * 2 * 2));
  }

  @Test
  void testPower2() {
    assertEquals(Power.of(RealScalar.ONE, new BigInteger("23847625384765238754826534")), RealScalar.ONE);
    assertEquals(Power.of(RealScalar.ONE, new BigInteger("-23847625384765238754826534")), RealScalar.ONE);
  }

  @Test
  void testPowerFractional() {
    Scalar lhs = Power.of(RationalScalar.of(-2, 3), 1.3);
    Scalar rhs = Scalars.fromString(" -  0.3469764892956748` - 0.47757216669512637` *I ");
    Chop._13.requireClose(lhs, rhs);
  }

  @Test
  void testSerializable() throws Exception {
    Scalar scalar = RationalScalar.of(3, 5);
    assertEquals(scalar, Serialization.parse(Serialization.of(scalar)));
    assertEquals(scalar, Serialization.copy(scalar));
  }

  @Test
  void testSort() {
    Tensor v = Tensors.of(RationalScalar.of(3, 4), RationalScalar.of(-1, 7), RealScalar.ZERO);
    Tensor s = Sort.of(v);
    Tensor r = Tensors.fromString("{-1/7, 0, 3/4}");
    assertEquals(s, r);
  }

  @Test
  void testCompare() {
    assertTrue(Scalars.lessThan(RationalScalar.of(-3, 2), RealScalar.ZERO));
    assertFalse(Scalars.lessThan(RationalScalar.of(3, 2), RealScalar.ZERO));
    assertFalse(Scalars.lessThan(RealScalar.ZERO, RationalScalar.of(-3, 2)));
    assertTrue(Scalars.lessThan(RealScalar.ZERO, RationalScalar.of(3, 2)));
    assertTrue(Scalars.lessThan(RationalScalar.of(-3, 2), RationalScalar.of(-3, 3)));
    assertTrue(Scalars.lessThan(RationalScalar.of(3, 20), RationalScalar.of(4, 2)));
    assertTrue(Scalars.lessThan(RationalScalar.of(-13, 20), RationalScalar.of(17, 2)));
  }

  @Test
  void testEquals() {
    assertEquals(RationalScalar.of(0, 1), RealScalar.ZERO);
    assertEquals(RationalScalar.of(0, 1), DoubleScalar.of(0));
    assertEquals(RealScalar.ZERO, RationalScalar.of(0, 1));
    assertEquals(DoubleScalar.of(0), RationalScalar.of(0, 1));
    assertEquals(DoubleScalar.of(123), RationalScalar.of(123, 1));
  }

  @Test
  void testNumber() {
    Scalar r = RealScalar.of(48962534765312235L);
    assertEquals(r.number().getClass(), Long.class);
    @SuppressWarnings("unused")
    long nothing = (Long) r.number();
  }

  @Test
  void testMixedDivision() {
    Scalar zero = RealScalar.ZERO;
    Scalar eps = DoubleScalar.of(Math.nextUp(0.0));
    assertEquals(zero.divide(eps), zero);
  }

  @Test
  void testIsInteger() {
    assertTrue(((RationalScalar) RationalScalar.of(0, 990)).isInteger());
    assertTrue(((RationalScalar) RationalScalar.of(0, -10)).isInteger());
    assertTrue(((RationalScalar) RationalScalar.of(5, 1)).isInteger());
    assertFalse(((RationalScalar) RationalScalar.of(1, 5)).isInteger());
    assertFalse(((RationalScalar) RationalScalar.of(5, 2)).isInteger());
    assertTrue(((RationalScalar) RationalScalar.of(5, -1)).isInteger());
    assertFalse(((RationalScalar) RationalScalar.of(1, -5)).isInteger());
    assertFalse(((RationalScalar) RationalScalar.of(5, -2)).isInteger());
    assertTrue(((RationalScalar) RationalScalar.of(-5, 1)).isInteger());
    assertFalse(((RationalScalar) RationalScalar.of(-1, 5)).isInteger());
    assertFalse(((RationalScalar) RationalScalar.of(-5, 2)).isInteger());
  }

  @Test
  void testDivideZeroFail() {
    assertThrows(ArithmeticException.class, () -> RealScalar.ONE.divide(RealScalar.ZERO));
  }

  @Test
  void testZeroUnderFail() {
    assertThrows(ArithmeticException.class, () -> RealScalar.ZERO.under(RealScalar.ONE));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> RationalScalar.of(null, BigInteger.ONE));
    assertThrows(NullPointerException.class, () -> RationalScalar.of(BigInteger.ONE, null));
  }
}
