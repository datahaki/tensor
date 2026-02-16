// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;

class QuantityCompareTest {
  private static void _checkEquals(Scalar s1, Scalar s2, boolean actual) {
    assertEquals(s1.equals(s2), s2.equals(s1));
    assertEquals(s1.equals(s2), actual);
  }

  @Test
  void testEquals() {
    _checkEquals(Quantity.of(2, "m"), RealScalar.of(2), false);
    _checkEquals(Quantity.of(0, "m"), RealScalar.of(0.0), false);
    _checkEquals(Quantity.of(0, "s"), RealScalar.of(0), false);
    _checkEquals(Quantity.of(0, "s*kg^2"), RealScalar.of(2), false);
  }

  @Test
  void testEquals2() {
    _checkEquals(Rational.of(0, 1), Quantity.of(0, "m"), false);
    _checkEquals(DoubleScalar.of(0.0), Quantity.of(0, "m"), false);
    _checkEquals(DecimalScalar.of(new BigDecimal("0.0")), Quantity.of(0, "m"), false);
  }

  @Test
  void testEquals3() {
    Scalar s1 = Quantity.of(2, "m");
    Scalar s2 = Quantity.of(2, "m^1.0");
    _checkEquals(s1, s2, true);
  }

  @Test
  void testCompareEquals() {
    Scalar q1 = Quantity.of(0, "s");
    Scalar q2 = Quantity.of(0, "rad");
    assertNotEquals(q1, q2);
    assertThrows(Throw.class, () -> Scalars.compare(q1, q2));
    assertThrows(Throw.class, () -> Scalars.compare(RealScalar.ZERO, q2));
  }

  @Test
  void testIsZero() {
    Scalar qs1 = Quantity.of(2, "m");
    Scalar qs2 = Quantity.of(3, "m");
    Scalar qs3 = Quantity.of(5, "m");
    assertTrue(Scalars.isZero(qs1.add(qs2).subtract(qs3)));
  }

  private static boolean _isNonNegative(Scalar scalar) {
    return Scalars.lessEquals(scalar.zero(), scalar);
  }

  @Test
  void testPredicate() {
    assertTrue(_isNonNegative(Quantity.of(3, "m^2")));
    assertTrue(_isNonNegative(Quantity.of(0, "s*kg")));
    assertFalse(_isNonNegative(Quantity.of(-3, "m")));
  }

  private static void _checkCompareTo(Scalar s1, Scalar s2, int value) {
    int res1 = +Scalars.compare(s1, s2);
    int res2 = -Scalars.compare(s2, s1);
    assertEquals(res1, res2);
    assertEquals(res1, value);
  }

  @Test
  void testCompare() {
    _checkCompareTo(Quantity.of(2, "m"), Quantity.of(3, "m"), Integer.compare(2, 3));
    _checkCompareTo(Quantity.of(-3, "m*s"), Quantity.of(7, "s*m"), Integer.compare(-3, 7));
  }

  @Test
  void testCompareFail() {
    assertThrows(Exception.class, () -> _checkCompareTo(Quantity.of(2, "m"), Quantity.of(2, "kg"), Integer.compare(2, 2)));
  }

  @Test
  void testCompareFail2() {
    assertThrows(Throw.class, () -> Scalars.compare(DoubleScalar.of(3.14), Quantity.of(0, "m*s")));
  }

  @Test
  void testDistinct() {
    Scalar qs0 = Quantity.of(0, Unit.ONE);
    Scalar qs1 = Quantity.of(0, "m");
    Scalar qs2 = Quantity.of(0, "kg");
    Scalar qs3 = Quantity.of(0, "s");
    Scalar qs4 = Quantity.of(0, "");
    assertInstanceOf(RealScalar.class, qs4);
    Tensor vec = Tensors.of(qs0, qs1, qs2, qs3, qs4);
    assertEquals(vec.stream().distinct().count(), 4);
  }
}
