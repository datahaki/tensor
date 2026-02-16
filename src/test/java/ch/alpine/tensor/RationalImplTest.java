// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RationalImplTest {
  @Test
  void testThrow() {
    assertThrows(Exception.class, () -> Rational.of(1, 0));
  }

  @Test
  void testEquals() {
    assertEquals(Rational.of(0, 1), RealScalar.ZERO);
    assertEquals(Rational.of(0, 1), DoubleScalar.of(0));
    assertEquals(RealScalar.ZERO, Rational.of(0, 1));
    assertEquals(DoubleScalar.of(0), Rational.of(0, 1));
    assertEquals(DoubleScalar.of(123), Rational.of(123, 1));
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
  void testNumberInteger() {
    assertInstanceOf(Integer.class, Rational.of(Integer.MAX_VALUE, 1).number());
    assertInstanceOf(Integer.class, Rational.of(Integer.MIN_VALUE, 1).number());
    assertInstanceOf(Long.class, Rational.of(Math.addExact((long) Integer.MAX_VALUE, +1), 1).number());
    assertInstanceOf(Long.class, Rational.of(Math.addExact((long) Integer.MIN_VALUE, -1), 1).number());
  }

  @Test
  void testNumberLong() {
    assertInstanceOf(Long.class, RealScalar.of(BigInteger.valueOf(Long.MAX_VALUE)).number());
    assertInstanceOf(Long.class, RealScalar.of(BigInteger.valueOf(Long.MIN_VALUE)).number());
    assertInstanceOf(BigInteger.class, RealScalar.of(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE)).number());
    assertInstanceOf(BigInteger.class, RealScalar.of(BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE)).number());
  }

  @ParameterizedTest
  @CsvSource({ "0/990, true", "0/(-10), true" })
  void testIsIntegerCsv(String string, boolean isInteger) {
    assertEquals(((Rational) Scalars.fromString(string)).isInteger(), isInteger);
  }

  @Test
  void testIsInteger() {
    assertTrue(((Rational) Rational.of(0, 990)).isInteger());
    assertTrue(((Rational) Rational.of(0, -10)).isInteger());
    assertTrue(((Rational) Rational.of(5, 1)).isInteger());
    assertFalse(((Rational) Rational.of(1, 5)).isInteger());
    assertFalse(((Rational) Rational.of(5, 2)).isInteger());
    assertTrue(((Rational) Rational.of(5, -1)).isInteger());
    assertFalse(((Rational) Rational.of(1, -5)).isInteger());
    assertFalse(((Rational) Rational.of(5, -2)).isInteger());
    assertTrue(((Rational) Rational.of(-5, 1)).isInteger());
    assertFalse(((Rational) Rational.of(-1, 5)).isInteger());
    assertFalse(((Rational) Rational.of(-5, 2)).isInteger());
  }
}
