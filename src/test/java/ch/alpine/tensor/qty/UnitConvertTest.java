// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.sca.Chop;

class UnitConvertTest {
  @Test
  void testKm() throws ClassNotFoundException, IOException {
    UnitConvert unitConvert = Serialization.copy(UnitConvert.SI());
    Scalar q = Quantity.of(2, "K*km^2");
    Unit unit = Unit.of("K*cm^2");
    Scalar scalar = unitConvert.to(unit).apply(q);
    assertEquals(scalar, Quantity.of(20000000000L, "K*cm^2"));
    ExactScalarQ.require(scalar);
  }

  @Test
  void testVelocity() {
    UnitConvert unitConvert = UnitConvert.SI();
    Scalar q = Quantity.of(360, "km*h^-1");
    Scalar scalar = unitConvert.to(Unit.of("m*s^-1")).apply(q);
    assertEquals(scalar, Quantity.of(100, "m*s^-1"));
  }

  @Test
  void testRadians() {
    UnitConvert unitConvert = UnitConvert.SI();
    Scalar q = Quantity.of(1, "rad");
    Scalar scalar = unitConvert.to(Unit.of("")).apply(q);
    assertEquals(scalar, Quantity.of(1, ""));
    ExactScalarQ.require(scalar);
  }

  @Test
  void testResistance() {
    UnitConvert unitConvert = UnitConvert.SI();
    Scalar q = Quantity.of(2, "mV^-1*mA*s^2");
    Scalar scalar = unitConvert.to(Unit.of("Ohm^-1*s^2")).apply(q);
    assertEquals(scalar, Quantity.of(2, "Ohm^-1*s^2"));
  }

  @Test
  void testForce() {
    Scalar force = UnitConvert.SI().to(Unit.of("N")).apply(Quantity.of(981, "cm*kg*s^-2"));
    assertEquals(force, Scalars.fromString("981/100[N]"));
    ExactScalarQ.require(force);
  }

  @Test
  void testNauticalMiles() {
    Scalar scalar = Quantity.of(1, "nmi");
    Scalar result = UnitConvert.SI().to(Unit.of("km")).apply(scalar);
    assertEquals(result, Scalars.fromString("1.852[km]"));
    ExactScalarQ.require(scalar);
    ExactScalarQ.require(result);
  }

  @Test
  void testNauticalMiles2() {
    Scalar scalar = Quantity.of(2, "nmi");
    Scalar result = UnitConvert.SI().to("km").apply(scalar);
    assertEquals(result, Scalars.fromString("3.704[km]"));
    ExactScalarQ.require(scalar);
    ExactScalarQ.require(result);
  }

  @Test
  void testLightYear() {
    Scalar scalar = Quantity.of(1, "ly");
    Scalar result = UnitConvert.SI().to("au").apply(scalar);
    ExactScalarQ.require(result);
    assertEquals(QuantityUnit.of(result), Unit.of("au"));
  }

  @Test
  void testKiloMega() {
    assertEquals(UnitSystem.SI().apply(Quantity.of(1e-3, "kHz")), Quantity.of(1, "s^-1"));
    assertEquals(UnitSystem.SI().apply(Quantity.of(1e-6, "MHz")), Quantity.of(1, "s^-1"));
    Scalar one_ohm = UnitSystem.SI().apply(Quantity.of(1, "Ohm"));
    assertEquals(UnitSystem.SI().apply(Quantity.of(1e-3, "kOhm")), one_ohm);
    assertEquals(UnitSystem.SI().apply(Quantity.of(1e-6, "MOhm")), one_ohm);
  }

  @Test
  void testKilowattHours() {
    Unit unit = Unit.of("kW*h");
    ScalarUnaryOperator suo = UnitConvert.SI().to(unit);
    Scalar scalar = suo.apply(Quantity.of(180, "W*s"));
    assertEquals(scalar, Quantity.of(RationalScalar.of(1, 20000), unit));
  }

  @Test
  void testPercent() {
    Scalar scalar = UnitConvert.SI().to("%").apply(RealScalar.of(0.5));
    Chop._05.requireClose(scalar, Quantity.of(50, "%"));
  }

  @Test
  void testToString() {
    String string = UnitConvert.SI().to("abc*h^-3").toString();
    assertTrue(string.startsWith("UnitConvert"));
    assertTrue(string.contains("abc*h^-3"));
  }

  @Test
  void testSome() {
    Scalar scalar = Quantity.of(3, "deg");
    Scalar degree = UnitConvert.SI().to("deg").apply(scalar);
    ExactScalarQ.require(degree);
    assertEquals(scalar, degree);
  }

  @Test
  void testNaNToPerc() {
    Scalar scalar = UnitConvert.SI().to("%").apply(DoubleScalar.INDETERMINATE);
    assertEquals(scalar.toString(), Quantity.of(DoubleScalar.INDETERMINATE, "%").toString());
    Scalar result = QuantityMagnitude.SI().in("%").apply(scalar);
    assertEquals(result.toString(), DoubleScalar.INDETERMINATE.toString());
  }

  @Test
  void test_kW_over_W() {
    Scalar scalar = Quantity.of(1, "kW*W^-1");
    System.out.println(UnitSystem.SI().apply(scalar));
  }

  @Test
  void testFail() {
    Scalar mass = Quantity.of(200, "g"); // gram
    Scalar a = Quantity.of(981, "cm*s^-2");
    Scalar force = mass.multiply(a);
    assertThrows(TensorRuntimeException.class, () -> UnitConvert.SI().to(Unit.of("m")).apply(force));
  }

  @Test
  void testFailInNull() {
    assertThrows(NullPointerException.class, () -> UnitConvert.SI().to((Unit) null));
  }

  @Test
  void testFailNull() {
    assertThrows(NullPointerException.class, () -> UnitConvert.of(null));
  }
}
