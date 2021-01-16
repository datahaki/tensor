// code by jph
package ch.ethz.idsc.tensor.qty;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class UnitSystemsTest extends TestCase {
  public void testNoEffect() {
    AssertFail.of(() -> UnitSystems.rotate(UnitSystem.SI(), "unknownUnit", "unknownUnit"));
    AssertFail.of(() -> UnitSystems.rotate(UnitSystem.SI(), "s", "kg"));
    AssertFail.of(() -> UnitSystems.rotate(UnitSystem.SI(), "s", "K"));
    AssertFail.of(() -> UnitSystems.rotate(UnitSystem.SI(), "s", "N"));
    // AssertFail.of(() -> );
  }

  public void testIncompatible() {
    UnitSystem rotate = UnitSystems.rotate(UnitSystem.SI(), "N", "s"); // not sure what this does
    assertEquals(UnitSystem.SI().map().size(), rotate.map().size());
  }

  public void testIncompatibleComposite() {
    UnitSystems.rotate(UnitSystem.SI(), "N", "kg*m");
  }

  public void testTrival() {
    UnitSystem unitSystem = UnitSystems.rotate(UnitSystem.SI(), "K", "K");
    assertEquals(unitSystem.map(), UnitSystem.SI().map());
    assertTrue(unitSystem.map().containsKey("K"));
    assertEquals(unitSystem.map().get("K"), Quantity.of(1, "K"));
  }

  public void testTrival2() {
    UnitSystem unitSystem = UnitSystems.rotate(UnitSystem.SI(), "m", "m");
    assertEquals(unitSystem.map(), UnitSystem.SI().map());
    assertFalse(unitSystem.map().containsKey("m"));
  }

  public void testMinutes() {
    UnitSystem unitSystem = UnitSystems.rotate(UnitSystem.SI(), "s", "min");
    assertEquals(unitSystem.apply(Quantity.of(1, "h")), Quantity.of(60, "min"));
  }

  public void testKgN() {
    assertTrue(UnitSystem.SI().map().containsKey("N"));
    UnitSystem unitSystem = UnitSystems.rotate(UnitSystem.SI(), "kg", "N");
    Scalar scalar = unitSystem.apply(Quantity.of(3, "kg*m*s^-1"));
    assertEquals(scalar, Quantity.of(3, "N*s"));
    assertFalse(unitSystem.map().containsKey("N"));
  }

  public void testCurrency() {
    UnitSystem baseSystem = SimpleUnitSystem.from(ResourceData.properties("/unit/chf.properties"));
    assertTrue(baseSystem.map().containsKey("EUR"));
    UnitSystem unitSystem = UnitSystems.rotate(baseSystem, "CHF", "EUR");
    assertFalse(unitSystem.map().containsKey("EUR"));
    Chop._06.requireClose(unitSystem.apply(Quantity.of(1, "USD")), Quantity.of(0.82784466, "EUR"));
    assertEquals(unitSystem.apply(Quantity.of(2, "m*s^-1")), Quantity.of(2, "m*s^-1"));
    Chop._06.requireClose(unitSystem.apply(Quantity.of(10, "CHF*m^-1")), Quantity.of(9.2902266, "EUR*m^-1"));
  }

  public void testIdentity() {
    UnitSystem baseSystem = SimpleUnitSystem.from(ResourceData.properties("/unit/chf.properties"));
    UnitSystem unitSystem = UnitSystems.rotate(baseSystem, "CHF", "CHF");
    assertTrue(unitSystem == baseSystem);
    assertFalse(unitSystem.map().containsKey("CHF"));
  }

  public void testkW() {
    UnitSystem baseSystem = UnitSystem.SI();
    UnitSystem unitSystem = UnitSystems.rotate(UnitSystems.rotate(baseSystem, "W", "kW"), "s", "h");
    Scalar scalar = Quantity.of(10, "W*s^-1");
    Scalar scalar2 = unitSystem.apply(scalar);
    assertEquals(scalar2, Quantity.of(36, "kW*h^-1"));
  }
}
