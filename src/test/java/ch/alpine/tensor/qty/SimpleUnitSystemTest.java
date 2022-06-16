// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.num.GaussScalar;

class SimpleUnitSystemTest {
  @Test
  void testInstance() {
    UnitSystem unitSystem = UnitSystem.SI();
    assertInstanceOf(SimpleUnitSystem.class, unitSystem);
  }

  @Test
  void testMap() throws ClassNotFoundException, IOException {
    Properties properties = new Properties();
    properties.setProperty("cent", "1/100[FRA]");
    UnitSystem unitSystem = Serialization.copy(SimpleUnitSystem.from(properties));
    Scalar scalar = unitSystem.apply(Quantity.of(100, "cent"));
    assertEquals(scalar, Quantity.of(1, "FRA"));
    assertEquals(unitSystem.map().size(), 1);
  }

  @Test
  void testFailKey1() {
    Properties properties = new Properties();
    properties.setProperty("cent123", "1/100[FRA]");
    assertThrows(IllegalArgumentException.class, () -> SimpleUnitSystem.from(properties));
  }

  @Test
  void testFailKey2() {
    Properties properties = new Properties();
    properties.setProperty(" cent", "1/100[FRA]");
    assertThrows(IllegalArgumentException.class, () -> SimpleUnitSystem.from(properties));
  }

  @Test
  void testFailValue1() {
    Properties properties = new Properties();
    properties.setProperty("cent", "1/100a[FRA]");
    assertThrows(TensorRuntimeException.class, () -> SimpleUnitSystem.from(properties));
  }

  @Test
  void testFailValue2() {
    Properties properties = new Properties();
    properties.setProperty("cent", "b/100a");
    assertThrows(TensorRuntimeException.class, () -> SimpleUnitSystem.from(properties));
  }

  @Test
  void testDerive() throws ClassNotFoundException, IOException {
    UnitSystem unitSystem = Serialization.copy(SimpleUnitSystem.from(UnitSystem.SI().map()));
    assertEquals(unitSystem.map(), UnitSystem.SI().map());
  }

  @Test
  void testXFree0Fail() {
    Map<String, Scalar> map = new HashMap<>();
    map.put("m", Quantity.of(GaussScalar.of(0, 17), "m"));
    assertThrows(IllegalArgumentException.class, () -> SimpleUnitSystem.from(map));
  }

  @Test
  void testXFree1() {
    Map<String, Scalar> map = new HashMap<>();
    map.put("m", Quantity.of(GaussScalar.of(1, 17), "m"));
    SimpleUnitSystem.from(map);
  }

  @Test
  void testXFree2Fail() {
    Map<String, Scalar> map = new HashMap<>();
    map.put("m", Quantity.of(GaussScalar.of(2, 17), "m"));
    assertThrows(IllegalArgumentException.class, () -> SimpleUnitSystem.from(map));
  }

  @Test
  void testEmpty() {
    UnitSystem unitSystem = SimpleUnitSystem.from(new Properties());
    assertTrue(unitSystem.map().isEmpty());
  }

  @Test
  void testDimensions() throws ClassNotFoundException, IOException {
    UnitSystem unitSystem = Serialization.copy(UnitSystem.SI());
    assertEquals(unitSystem.dimensions(Unit.of("N")), Unit.of("kg*m*s^-2"));
    assertEquals(unitSystem.dimensions(Unit.of("km")), Unit.of("m"));
    assertEquals(unitSystem.dimensions(Unit.of("km*h^3")), Unit.of("m*s^3"));
    assertEquals(unitSystem.dimensions(Unit.of("kW*h")), Unit.of("kg*m^2*s^-2"));
    assertEquals(unitSystem.dimensions(Unit.of("xknown^3")), Unit.of("xknown^3"));
    assertEquals(unitSystem.dimensions(Unit.of("xkn*own^3")), Unit.of("xkn*own^3"));
    assertEquals(unitSystem.dimensions(Unit.of("g^-2*xkn*own^3")), Unit.of("kg^-2*xkn*own^3"));
  }

  @Test
  void testDimensionsFail() {
    assertThrows(NullPointerException.class, () -> UnitSystem.SI().apply(null));
    assertThrows(NullPointerException.class, () -> UnitSystem.SI().dimensions(null));
  }

  @Test
  void testTogs() {
    ScalarUnaryOperator suo = QuantityMagnitude.SI().in("m^2*K*W^-1");
    Scalar scalar = suo.apply(Quantity.of(10, "togs"));
    assertEquals(scalar, RealScalar.ONE);
  }

  @Test
  void testFailNullProperties() {
    assertThrows(NullPointerException.class, () -> SimpleUnitSystem.from((Properties) null));
  }

  @Test
  void testFailNullMap() {
    assertThrows(NullPointerException.class, () -> SimpleUnitSystem.from((Map<String, Scalar>) null));
  }
}
