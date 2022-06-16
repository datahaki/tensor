// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.sca.Chop;

class UnitSystemsTest {
  private static void checkInvariant(UnitSystem unitSystem) {
    for (Entry<String, Scalar> entry : unitSystem.map().entrySet()) {
      Scalar scalar = Quantity.of(1, entry.getKey());
      assertEquals(unitSystem.apply(scalar), entry.getValue());
      assertEquals(unitSystem.apply(entry.getValue()), entry.getValue());
    }
  }

  @Test
  void testKnownAtoms() {
    KnownUnitQ knownUnitQ = KnownUnitQ.SI();
    assertTrue(knownUnitQ.test(Unit.of("")));
    assertTrue(knownUnitQ.test(Unit.of("K")));
    assertTrue(knownUnitQ.test(Unit.of("m")));
    assertTrue(knownUnitQ.test(Unit.of("kW")));
    assertTrue(knownUnitQ.test(Unit.of("kW*s")));
    for (String base : UnitSystems.base(UnitSystem.SI())) {
      assertTrue(knownUnitQ.test(Unit.of(base)));
      assertTrue(knownUnitQ.test(Unit.of(base + "^2")));
    }
  }

  @Test
  void testNoEffect() {
    assertThrows(IllegalArgumentException.class, () -> UnitSystems.rotate(UnitSystem.SI(), "unknownUnit", "unknownUnit"));
    assertThrows(NullPointerException.class, () -> UnitSystems.rotate(UnitSystem.SI(), "s", "kg"));
    assertThrows(NullPointerException.class, () -> UnitSystems.rotate(UnitSystem.SI(), "s", "K"));
    assertThrows(NullPointerException.class, () -> UnitSystems.rotate(UnitSystem.SI(), "N", "K"));
    assertThrows(NullPointerException.class, () -> UnitSystems.rotate(UnitSystem.SI(), "N", "kg"));
    assertThrows(NullPointerException.class, () -> UnitSystems.rotate(UnitSystem.SI(), "N", "s"));
    assertThrows(NullPointerException.class, () -> UnitSystems.rotate(UnitSystem.SI(), "W", "kW"));
    assertThrows(NullPointerException.class, () -> UnitSystems.rotate(UnitSystem.SI(), "kW", "W"));
  }

  @Test
  void testSize() {
    checkInvariant(UnitSystem.SI());
  }

  private static UnitSystem requireInvariant(UnitSystem unitSystem, String prev, String next) {
    UnitSystem u1 = UnitSystems.rotate(unitSystem, prev, next);
    assertEquals(u1.map().size(), unitSystem.map().size());
    UnitSystem u2 = UnitSystems.rotate(u1, next, prev);
    for (Entry<String, Scalar> entry : unitSystem.map().entrySet()) {
      Scalar scalar = u2.map().get(entry.getKey());
      Chop._07.requireClose(entry.getValue(), scalar);
    }
    checkInvariant(u1);
    checkInvariant(u2);
    return u1;
  }

  @Test
  void testTrival() {
    UnitSystem unitSystem = UnitSystems.rotate(UnitSystem.SI(), "K", "K");
    checkInvariant(unitSystem);
    assertEquals(unitSystem.map(), UnitSystem.SI().map());
  }

  @Test
  void testTrival2() {
    UnitSystem unitSystem = UnitSystems.rotate(UnitSystem.SI(), "m", "m");
    checkInvariant(unitSystem);
    assertEquals(unitSystem.map(), UnitSystem.SI().map());
  }

  @Test
  void testEquivalentMinutes() {
    UnitSystem unitSystem = requireInvariant(UnitSystem.SI(), "s", "min");
    assertEquals(unitSystem.apply(Quantity.of(1, "h")), Quantity.of(60, "min"));
    assertEquals(unitSystem.map().get("s"), Quantity.of(RationalScalar.of(1, 60), "min"));
    assertFalse(unitSystem.map().containsKey("min"));
    for (Entry<String, Scalar> entry : unitSystem.map().entrySet())
      assertFalse(QuantityUnit.of(entry.getValue()).map().containsKey("s"));
  }

  @Test
  void testEquivalentHours() {
    UnitSystem unitSystem = requireInvariant(UnitSystem.SI(), "s", "h");
    Scalar scalar = Quantity.of(1, "uW*wk"); // W = m^2*kg*s^-3
    assertEquals(unitSystem.apply(scalar), Quantity.of(7838208, "h^-2*kg*m^2"));
  }

  @Test
  void testEquivalentKilometers() {
    UnitSystem unitSystem = requireInvariant(UnitSystem.SI(), "m", "km");
    Scalar scalar = Quantity.of(1, "N");
    assertEquals(unitSystem.apply(scalar), Quantity.of(RationalScalar.of(1, 1000), "kg*km*s^-2"));
  }

  @Test
  void testEquivalentHertz() {
    UnitSystem unitSystem = requireInvariant(UnitSystem.SI(), "s", "Hz");
    assertEquals(unitSystem.apply(Quantity.of(1, "h")), Quantity.of(3600, "Hz^-1"));
    for (Entry<String, Scalar> entry : unitSystem.map().entrySet())
      assertFalse(QuantityUnit.of(entry.getValue()).map().containsKey("s"));
  }

  @Test
  void testSubstituteKgN() {
    UnitSystem unitSystem = requireInvariant(UnitSystem.SI(), "kg", "N");
    assertEquals(unitSystem.apply(Quantity.of(3, "kg*m*s^-1")), Quantity.of(3, "N*s"));
    assertFalse(unitSystem.map().containsKey("N"));
    for (Entry<String, Scalar> entry : unitSystem.map().entrySet())
      assertFalse(QuantityUnit.of(entry.getValue()).map().containsKey("kg"));
    assertEquals(unitSystem.apply(Quantity.of(1, "t")), Scalars.fromString("1000[N*m^-1*s^2]"));
  }

  @Test
  void testSubstituteSecondsN() {
    UnitSystem unitSystem = requireInvariant(UnitSystem.SI(), "s", "N");
    assertFalse(unitSystem.map().containsKey("N"));
    for (Entry<String, Scalar> entry : unitSystem.map().entrySet())
      assertFalse(QuantityUnit.of(entry.getValue()).map().containsKey("s"));
    Scalar origin = Quantity.of(1, "h");
    Scalar result = unitSystem.apply(origin);
    assertEquals(result, Scalars.fromString("3600[N^-1/2*m^1/2*kg^1/2]"));
    assertEquals(UnitSystem.SI().apply(result), Quantity.of(3600, "s"));
  }

  @Test
  void testSubstituteM_W() {
    UnitSystem unitSystem = requireInvariant(UnitSystem.SI(), "m", "W"); // W = m^2*kg*s^-3
    Scalar scalar = unitSystem.apply(Quantity.of(1, "km"));
    assertEquals(scalar, Scalars.fromString("1000[W^1/2*kg^-1/2*s^3/2]"));
    assertEquals(UnitSystem.SI().apply(scalar), Quantity.of(1000, "m"));
  }
  // public void testSubstituteM_kW() {
  // UnitSystem unitSystem = requireInvariant(UnitSystem.SI(), "m", "kW"); // W = m^2*kg*s^-3
  // Scalar scalar = unitSystem.apply(Quantity.of(1, "km"));
  // Tolerance.CHOP.requireClose(scalar, Scalars.fromString("31.622776601683793[kW^1/2*kg^-1/2*s^3/2]"));
  // Tolerance.CHOP.requireClose(UnitSystem.SI().apply(scalar), Quantity.of(1000, "m"));
  // }

  @Test
  void testCurrency() {
    UnitSystem baseSystem = SimpleUnitSystem.from(ResourceData.properties("/ch/alpine/tensor/qty/chf.properties"));
    assertTrue(baseSystem.map().containsKey("EUR"));
    UnitSystem unitSystem = requireInvariant(baseSystem, "CHF", "EUR");
    assertFalse(unitSystem.map().containsKey("EUR"));
    Chop._06.requireClose(unitSystem.apply(Quantity.of(1, "USD")), Quantity.of(0.82784466, "EUR"));
    assertEquals(unitSystem.apply(Quantity.of(2, "m*s^-1")), Quantity.of(2, "m*s^-1"));
    Chop._06.requireClose(unitSystem.apply(Quantity.of(10, "CHF*m^-1")), Quantity.of(9.2902266, "EUR*m^-1"));
  }

  @Test
  void testIdentity() throws ClassNotFoundException, IOException {
    UnitSystem baseSystem = SimpleUnitSystem.from(ResourceData.properties("/ch/alpine/tensor/qty/chf.properties"));
    UnitSystem unitSystem = requireInvariant(baseSystem, "CHF", "CHF");
    assertTrue(unitSystem == baseSystem);
    assertFalse(unitSystem.map().containsKey("CHF"));
    UnitSystem joined = UnitSystems.join(baseSystem, UnitSystem.SI());
    Serialization.copy(joined);
  }

  @Test
  void testSame() {
    UnitSystem s1 = UnitSystem.SI();
    UnitSystem s2 = UnitSystem.SI();
    UnitSystem s3 = UnitSystems.join(s1, s2);
    assertEquals(s1.map(), s3.map());
    assertThrows(UnsupportedOperationException.class, () -> s1.map().clear());
    assertThrows(UnsupportedOperationException.class, () -> s3.map().clear());
  }

  @Test
  void testJoinFail() {
    UnitSystem s1 = SimpleUnitSystem.from(Map.of("ym", Quantity.of(10, "m")));
    UnitSystem s2 = SimpleUnitSystem.from(Map.of("ym", Quantity.of(100, "m")));
    UnitSystems.join(s1, s1);
    assertThrows(TensorRuntimeException.class, () -> UnitSystems.join(s1, s2));
  }
}
