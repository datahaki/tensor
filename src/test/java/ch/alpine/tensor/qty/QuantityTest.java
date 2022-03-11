// code by jph
package ch.alpine.tensor.qty;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DeterminateScalarQ;
import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class QuantityTest extends TestCase {
  public void testFromString() {
    assertTrue(Scalars.fromString("-7[m*kg^-2]") instanceof Quantity);
    assertTrue(Scalars.fromString("3 [ m ]") instanceof Quantity);
    assertTrue(Scalars.fromString("3 [ m *rad ]  ") instanceof Quantity);
    assertFalse(Scalars.fromString(" 3  ") instanceof Quantity);
    assertFalse(Scalars.fromString(" 3 [] ") instanceof Quantity);
  }

  public void testNumberUnit() {
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

  public void testStringScalar() {
    _check("-7[m][kg]");
    _check("-7[m]a");
    _check("-7[m*kg^-2");
    _check("1abc[m]");
    _check("1abc[]");
  }

  public void testPercent() {
    Scalar of = Quantity.of(50, "%");
    DeterminateScalarQ.require(of);
    Scalar pr = Scalars.fromString("50[%]");
    assertEquals(of, pr);
    Scalar n1 = UnitSystem.SI().apply(pr);
    ExactScalarQ.require(n1);
    assertEquals(n1, RationalScalar.HALF);
    Scalar n2 = QuantityMagnitude.SI().in(Unit.ONE).apply(pr);
    assertEquals(n2, RationalScalar.HALF);
  }

  public void testFromStringComplex() {
    Quantity quantity = (Quantity) Scalars.fromString("(-7+3*I)[kg^-2*m*s]");
    Scalar scalar = quantity.value();
    assertEquals(scalar, ComplexScalar.of(-7, 3));
  }

  public void testDecimal() {
    Quantity quantity = (Quantity) Scalars.fromString("-7.23459823746593784659387465`13.0123[m*kg^-2]");
    assertTrue(quantity.value() instanceof DecimalScalar);
  }

  public void testParseFail() {
    AssertFail.of(() -> Quantity.of(3.14, "^2"));
    AssertFail.of(() -> Quantity.of(3.14, "m^2a"));
    AssertFail.of(() -> Quantity.of(3.14, "m^"));
    AssertFail.of(() -> Quantity.of(3.14, "m[^2"));
    AssertFail.of(() -> Quantity.of(3.14, "m]^2"));
  }

  public void testNestFail() {
    Scalar q1 = Quantity.of(3.14, "m");
    AssertFail.of(() -> Quantity.of(q1, "s"));
  }

  public void testNestEmptyFail() {
    AssertFail.of(() -> Quantity.of(Quantity.of(2, "s"), ""));
  }

  public void testValue() {
    Quantity quantity = (Quantity) Scalars.fromString("-7+3*I[kg^-2*m*s]");
    Scalar scalar = quantity.value();
    assertEquals(scalar, ComplexScalar.of(-7, 3));
  }

  public void testUnitString() {
    Quantity quantity = (Quantity) Scalars.fromString("-7+3*I[kg^-2*m*s]");
    String string = quantity.unit().toString();
    assertEquals(string, "kg^-2*m*s");
  }

  public void testEmptyPass() {
    assertEquals(Quantity.of(3.14, ""), RealScalar.of(3.14));
  }

  public void testPowerZeroExact() {
    Scalar scalar = Power.of(Quantity.of(3, "s^3*m^-1"), 0);
    ExactScalarQ.require(scalar);
    assertEquals(scalar, RealScalar.ONE);
  }

  public void testPowerZeroNumeric() {
    Scalar scalar = Power.of(Quantity.of(Pi.HALF.negate(), "s^3*m^-1"), 0);
    assertEquals(scalar, RealScalar.ONE);
  }
}
