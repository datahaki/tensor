// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.lie.Quaternion;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.Unit;

class SignTest {
  @Test
  public void testSome() {
    assertEquals(Sign.FUNCTION.apply(DoubleScalar.of(-345)), RealScalar.of(-1));
    assertEquals(Sign.FUNCTION.apply(DoubleScalar.of(9324.5)), RealScalar.of(1));
    assertEquals(Sign.FUNCTION.apply(RealScalar.ZERO), RealScalar.ZERO);
  }

  @Test
  public void testDiff() {
    assertEquals(Sign.FUNCTION.apply(RealScalar.of(3 - 9)), RealScalar.ONE.negate());
    assertEquals(Sign.FUNCTION.apply(RealScalar.of(3 - 3)), RealScalar.ZERO);
    assertEquals(Sign.FUNCTION.apply(RealScalar.of(9 - 3)), RealScalar.ONE);
  }

  @Test
  public void testDoubleNegZero() {
    Scalar d1 = DoubleScalar.of(-0.0);
    Sign.requirePositiveOrZero(d1);
    assertEquals(d1.toString(), "-0.0");
    Scalar s1 = Sign.FUNCTION.apply(d1);
    assertEquals(s1.toString(), "0");
    assertEquals(d1, d1.zero());
  }

  @Test
  public void testInfinity() {
    assertEquals(Sign.FUNCTION.apply(DoubleScalar.POSITIVE_INFINITY), RealScalar.of(+1));
    assertEquals(Sign.FUNCTION.apply(DoubleScalar.NEGATIVE_INFINITY), RealScalar.of(-1));
    assertTrue(Sign.isPositive(DoubleScalar.POSITIVE_INFINITY));
    assertTrue(Sign.isNegative(DoubleScalar.NEGATIVE_INFINITY));
  }

  @Test
  public void testIsNegative() {
    Unit apples = Unit.of("Apples");
    assertTrue(Sign.isNegative(Quantity.of(-2, apples)));
    assertFalse(Sign.isNegative(Quantity.of(0, apples)));
    assertFalse(Sign.isNegative(Quantity.of(2, apples)));
  }

  @Test
  public void testIsPositive() {
    assertFalse(Sign.isPositive(Quantity.of(-2, "V*A")));
    assertFalse(Sign.isPositive(Quantity.of(0, "V*A")));
    assertTrue(Sign.isPositive(Quantity.of(2, "V*A")));
    assertFalse(Sign.isPositive(Quantity.of(-2, Unit.ONE)));
    assertFalse(Sign.isPositive(Quantity.of(0, Unit.ONE)));
    assertTrue(Sign.isPositive(Quantity.of(2, Unit.ONE)));
  }

  @Test
  public void testIsPositiveOrZero() {
    Unit apples = Unit.of("Apples");
    assertFalse(Sign.isPositiveOrZero(Quantity.of(-2, apples)));
    assertTrue(Sign.isPositiveOrZero(Quantity.of(0, apples)));
    assertTrue(Sign.isPositiveOrZero(Quantity.of(2, apples)));
  }

  @Test
  public void testIsNegativeOrZero() {
    assertTrue(Sign.isNegativeOrZero(Quantity.of(-2, "V*A")));
    assertTrue(Sign.isNegativeOrZero(Quantity.of(0, "V*A")));
    assertFalse(Sign.isNegativeOrZero(Quantity.of(2, "V*A")));
    assertTrue(Sign.isNegativeOrZero(Quantity.of(-2, Unit.ONE)));
    assertTrue(Sign.isNegativeOrZero(Quantity.of(0, Unit.ONE)));
    assertFalse(Sign.isNegativeOrZero(Quantity.of(2, Unit.ONE)));
  }

  @Test
  public void testRequireNonNegative() {
    Sign.requirePositiveOrZero(RealScalar.ZERO);
    Sign.requirePositiveOrZero(RealScalar.ONE);
    Sign.requirePositiveOrZero(Quantity.of(2, "m*s^-2"));
    assertThrows(TensorRuntimeException.class, () -> Sign.requirePositiveOrZero(RealScalar.ONE.negate()));
    assertThrows(TensorRuntimeException.class, () -> Sign.requirePositiveOrZero(DoubleScalar.INDETERMINATE));
  }

  @Test
  public void testRequirePositive() {
    Sign.requirePositive(RealScalar.ONE);
    Sign.requirePositive(Quantity.of(2, "m*s^-2"));
    assertThrows(TensorRuntimeException.class, () -> Sign.requirePositive(RealScalar.ZERO));
    assertThrows(TensorRuntimeException.class, () -> Sign.requirePositive(RealScalar.ONE.negate()));
    assertThrows(TensorRuntimeException.class, () -> Sign.requirePositive(DoubleScalar.INDETERMINATE));
  }

  @Test
  public void testNaN() {
    assertEquals(Sign.FUNCTION.apply(DoubleScalar.INDETERMINATE).toString(), "NaN");
  }

  private static void _checkFailAll(Scalar value) {
    assertThrows(TensorRuntimeException.class, () -> Sign.FUNCTION.apply(value));
    _checkSignIntFail(value);
  }

  private static void _checkSignIntFail(Scalar value) {
    assertThrows(Exception.class, () -> Sign.isPositive(value));
    assertThrows(Exception.class, () -> Sign.isNegative(value));
    assertThrows(Exception.class, () -> Sign.isPositiveOrZero(value));
    assertThrows(Exception.class, () -> Sign.isNegativeOrZero(value));
  }

  @Test
  public void testFail() {
    _checkSignIntFail(ComplexScalar.of(2, 3));
    _checkSignIntFail(Quaternion.of(RealScalar.of(-4), Tensors.vector(1, 2, 3)));
    // _checkFailAll(DoubleScalar.INDETERMINATE);
    _checkFailAll(StringScalar.of("string"));
  }
}
