// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.sca.pow.Power;

class QuantityTest {
  @Test
  void testFromString() {
    assertEquals(Scalars.fromString("-7[m*kg^-2]"), Quantity.of(-7, "m*kg^-2"));
    assertEquals(Scalars.fromString("3 [ m ]"), Quantity.of(3, "m"));
    assertEquals(Scalars.fromString("3 [ m *rad ]  "), Quantity.of(3, "m*rad"));
    assertFalse(Scalars.fromString(" 3  ") instanceof Quantity);
    assertFalse(Scalars.fromString(" 3 [] ") instanceof Quantity);
  }

  @Test
  void testNumberUnit() {
    Unit unit = Unit.of("m*s^-1");
    Scalar scalar = Quantity.of(3, unit);
    assertEquals(scalar, Scalars.fromString("3[m*s^-1]"));
    assertEquals(scalar, Quantity.of(3, "m*s^-1"));
    assertEquals(scalar, Quantity.of(RealScalar.of(3), "m*s^-1"));
    assertEquals(scalar, Quantity.of(RealScalar.of(3), unit));
  }

  private static void _check(String string) {
    Scalar scalar = Scalars.fromString(string);
    assertEquals(scalar.getClass(), StringScalar.class);
    assertEquals(scalar.toString(), string);
  }

  @Test
  void testStringScalar() {
    _check("-7[m][kg]");
    _check("-7[m]a");
    _check("-7[m*kg^-2");
    _check("1abc[m]");
    _check("1abc[]");
  }

  @Test
  void testAddZero() {
    Scalar a = Quantity.of(0, "m");
    Scalar b = Quantity.of(0, "n");
    Scalar c = Quantity.of(0, "");
    assertThrows(Exception.class, () -> a.add(b));
    assertThrows(Exception.class, () -> b.add(c));
    assertThrows(Exception.class, () -> c.add(a));
  }

  @Test
  void testPercent() {
    Scalar of = Quantity.of(50, "%");
    assertTrue(FiniteScalarQ.of(of));
    Scalar pr = Scalars.fromString("50[%]");
    assertEquals(of, pr);
    Scalar n1 = UnitSystem.SI().apply(pr);
    ExactScalarQ.require(n1);
    assertEquals(n1, Rational.HALF);
    Scalar n2 = QuantityMagnitude.SI().in(Unit.ONE).apply(pr);
    assertEquals(n2, Rational.HALF);
  }

  @Test
  void testFromStringComplex() {
    Quantity quantity = (Quantity) Scalars.fromString("(-7+3*I)[kg^-2*m*s]");
    Scalar scalar = quantity.value();
    assertEquals(scalar, ComplexScalar.of(-7, 3));
  }

  @Test
  void testDecimal() {
    Quantity quantity = (Quantity) Scalars.fromString("-7.23459823746593784659387465`13.0123[m*kg^-2]");
    assertInstanceOf(DecimalScalar.class, quantity.value());
  }

  @Test
  void testParseFail() {
    assertThrows(IllegalArgumentException.class, () -> Quantity.of(3.14, "^2"));
    assertThrows(Throw.class, () -> Quantity.of(3.14, "m^2a"));
    assertThrows(Throw.class, () -> Quantity.of(3.14, "m^"));
    assertThrows(IllegalArgumentException.class, () -> Quantity.of(3.14, "m[^2"));
    assertThrows(IllegalArgumentException.class, () -> Quantity.of(3.14, "m]^2"));
  }

  @Test
  void testNestFail() {
    Scalar q1 = Quantity.of(3.14, "m");
    assertThrows(Throw.class, () -> Quantity.of(q1, "s"));
  }

  @Test
  void testNestEmptyFail() {
    assertThrows(Throw.class, () -> Quantity.of(Quantity.of(2, "s"), ""));
  }

  @Test
  void testValue() {
    Quantity quantity = (Quantity) Scalars.fromString("-7+3*I[kg^-2*m*s]");
    Scalar scalar = quantity.value();
    assertEquals(scalar, ComplexScalar.of(-7, 3));
    ExactScalarQ.require(quantity);
  }

  @Test
  void testRounding() {
    Scalar scalar = Scalars.fromString("-7.2+3.7*I[kg^-1*m^2*s]");
    assertFalse(ExactScalarQ.of(scalar));
    assertInstanceOf(Quantity.class, scalar);
    Scalar round = Round.FUNCTION.apply(scalar);
    assertInstanceOf(Quantity.class, round);
    assertEquals(round, Scalars.fromString("-7+4*I[kg^-1*m^2*s]"));
    ExactScalarQ.require(round);
    assertTrue(FiniteScalarQ.of(round));
  }

  @Test
  void testUnitString() {
    Quantity quantity = (Quantity) Scalars.fromString("-7+3*I[kg^-2*m*s]");
    String string = quantity.unit().toString();
    assertEquals(string, "kg^-2*m*s");
  }

  @Test
  void testEmptyPass() {
    assertEquals(Quantity.of(3.14, ""), RealScalar.of(3.14));
  }

  @Test
  void testPowerZeroExact() {
    Scalar scalar = Power.of(Quantity.of(3, "s^3*m^-1"), 0);
    ExactScalarQ.require(scalar);
    assertEquals(scalar, RealScalar.ONE);
  }

  @Test
  void testPowerZeroNumeric() {
    Scalar scalar = Power.of(Quantity.of(Pi.HALF.negate(), "s^3*m^-1"), 0);
    assertEquals(scalar, RealScalar.ONE);
  }
}
