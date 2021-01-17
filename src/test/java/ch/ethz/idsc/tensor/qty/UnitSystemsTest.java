// code by jph
package ch.ethz.idsc.tensor.qty;

import java.util.Map.Entry;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class UnitSystemsTest extends TestCase {
  public void testNoEffect() {
    AssertFail.of(() -> UnitSystems.rotate(UnitSystem.SI(), "unknownUnit", "unknownUnit"));
    AssertFail.of(() -> UnitSystems.rotate(UnitSystem.SI(), "s", "kg"));
    AssertFail.of(() -> UnitSystems.rotate(UnitSystem.SI(), "s", "K"));
    AssertFail.of(() -> UnitSystems.rotate(UnitSystem.SI(), "N", "K"));
    AssertFail.of(() -> UnitSystems.rotate(UnitSystem.SI(), "N", "kg"));
    AssertFail.of(() -> UnitSystems.rotate(UnitSystem.SI(), "N", "s"));
    AssertFail.of(() -> UnitSystems.rotate(UnitSystem.SI(), "W", "kW"));
    AssertFail.of(() -> UnitSystems.rotate(UnitSystem.SI(), "kW", "W"));
  }

  private static UnitSystem requireInvariant(UnitSystem unitSystem, String prev, String next) {
    UnitSystem u1 = UnitSystems.rotate(unitSystem, prev, next);
    assertEquals(u1.map().size(), unitSystem.map().size());
    UnitSystem u2 = UnitSystems.rotate(u1, next, prev);
    for (Entry<String, Scalar> entry : unitSystem.map().entrySet()) {
      Scalar scalar = u2.map().get(entry.getKey());
      // if (!entry.getValue().equals(scalar)) {
      if (!Chop._10.isClose(entry.getValue(), scalar)) {
        // System.err.println(entry.getKey());
      }
      Chop._10.requireClose(entry.getValue(), scalar);
    }
    TestHelper.checkInvariant(u1);
    TestHelper.checkInvariant(u2);
    return u1;
  }

  public void testTrival() {
    UnitSystem unitSystem = UnitSystems.rotate(UnitSystem.SI(), "K", "K");
    TestHelper.checkInvariant(unitSystem);
    assertEquals(unitSystem.map(), UnitSystem.SI().map());
  }

  public void testTrival2() {
    UnitSystem unitSystem = UnitSystems.rotate(UnitSystem.SI(), "m", "m");
    TestHelper.checkInvariant(unitSystem);
    assertEquals(unitSystem.map(), UnitSystem.SI().map());
  }

  public void testEquivalentMinutes() {
    UnitSystem unitSystem = requireInvariant(UnitSystem.SI(), "s", "min");
    assertEquals(unitSystem.apply(Quantity.of(1, "h")), Quantity.of(60, "min"));
    assertEquals(unitSystem.map().get("s"), Quantity.of(RationalScalar.of(1, 60), "min"));
    assertFalse(unitSystem.map().containsKey("min"));
    for (Entry<String, Scalar> entry : unitSystem.map().entrySet())
      assertFalse(QuantityUnit.of(entry.getValue()).map().containsKey("s"));
  }

  public void testEquivalentHours() {
    UnitSystem unitSystem = requireInvariant(UnitSystem.SI(), "s", "h");
    Scalar scalar = Quantity.of(1, "uW*wk"); // W = m^2*kg*s^-3
    assertEquals(unitSystem.apply(scalar), Quantity.of(7838208, "h^-2*kg*m^2"));
  }

  public void testEquivalentKilometers() {
    UnitSystem unitSystem = requireInvariant(UnitSystem.SI(), "m", "km");
    Scalar scalar = Quantity.of(1, "N"); // W = m^2*kg*s^-3
    assertEquals(unitSystem.apply(scalar), Quantity.of(RationalScalar.of(1, 1000), "kg*km*s^-2"));
  }

  public void testEquivalentHertz() {
    UnitSystem unitSystem = requireInvariant(UnitSystem.SI(), "s", "Hz");
    // Scalar scalar = ; // W = m^2*kg*s^-3
    // System.out.println(unitSystem.apply(scalar));
    assertEquals(unitSystem.apply(Quantity.of(1, "h")), Quantity.of(3600, "Hz^-1"));
    for (Entry<String, Scalar> entry : unitSystem.map().entrySet())
      assertFalse(QuantityUnit.of(entry.getValue()).map().containsKey("s"));
  }

  public void testSubstituteKgN() {
    UnitSystem unitSystem = requireInvariant(UnitSystem.SI(), "kg", "N");
    assertEquals(unitSystem.apply(Quantity.of(3, "kg*m*s^-1")), Quantity.of(3, "N*s"));
    assertFalse(unitSystem.map().containsKey("N"));
    for (Entry<String, Scalar> entry : unitSystem.map().entrySet())
      assertFalse(QuantityUnit.of(entry.getValue()).map().containsKey("kg"));
    assertEquals(unitSystem.apply(Quantity.of(1, "t")), Scalars.fromString("1000[N*m^-1*s^2]"));
  }

  public void testSubstituteSecondsN() {
    UnitSystem unitSystem = requireInvariant(UnitSystem.SI(), "s", "N");
    assertFalse(unitSystem.map().containsKey("N"));
    for (Entry<String, Scalar> entry : unitSystem.map().entrySet())
      assertFalse(QuantityUnit.of(entry.getValue()).map().containsKey("s"));
    Scalar origin = Quantity.of(1, "h");
    Scalar result = unitSystem.apply(origin);
    assertEquals(result, Scalars.fromString("3600[N^-1/2*m^1/2*kg^1/2]"));
    assertEquals(UnitSystem.SI().apply(result), Quantity.of(3600, "s"));
  }

  public void testSubstituteM_W() {
    UnitSystem unitSystem = requireInvariant(UnitSystem.SI(), "m", "W"); // W = m^2*kg*s^-3
    Scalar scalar = unitSystem.apply(Quantity.of(1, "km"));
    assertEquals(scalar, Scalars.fromString("1000[W^1/2*kg^-1/2*s^3/2]"));
    assertEquals(UnitSystem.SI().apply(scalar), Quantity.of(1000, "m"));
  }

  public void testSubstituteM_kW() {
    UnitSystem unitSystem = requireInvariant(UnitSystem.SI(), "m", "kW"); // W = m^2*kg*s^-3
    Scalar scalar = unitSystem.apply(Quantity.of(1, "km"));
    Chop._10.requireClose(scalar, Scalars.fromString("31.622776601683793[kW^1/2*kg^-1/2*s^3/2]"));
    Chop._09.requireClose(UnitSystem.SI().apply(scalar), Quantity.of(1000, "m"));
  }

  public void testCurrency() {
    UnitSystem baseSystem = SimpleUnitSystem.from(ResourceData.properties("/unit/chf.properties"));
    assertTrue(baseSystem.map().containsKey("EUR"));
    UnitSystem unitSystem = requireInvariant(baseSystem, "CHF", "EUR");
    assertFalse(unitSystem.map().containsKey("EUR"));
    Chop._06.requireClose(unitSystem.apply(Quantity.of(1, "USD")), Quantity.of(0.82784466, "EUR"));
    assertEquals(unitSystem.apply(Quantity.of(2, "m*s^-1")), Quantity.of(2, "m*s^-1"));
    Chop._06.requireClose(unitSystem.apply(Quantity.of(10, "CHF*m^-1")), Quantity.of(9.2902266, "EUR*m^-1"));
  }

  public void testIdentity() {
    UnitSystem baseSystem = SimpleUnitSystem.from(ResourceData.properties("/unit/chf.properties"));
    UnitSystem unitSystem = requireInvariant(baseSystem, "CHF", "CHF");
    assertTrue(unitSystem == baseSystem);
    assertFalse(unitSystem.map().containsKey("CHF"));
    UnitSystems.join(baseSystem, UnitSystem.SI());
  }
}
