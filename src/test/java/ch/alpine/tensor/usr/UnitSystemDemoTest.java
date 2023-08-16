// code by jph
package ch.alpine.tensor.usr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.qty.UnitSystem;
import ch.alpine.tensor.qty.UnitSystems;

class UnitSystemDemoTest {
  @Test
  void testLoad() {
    UnitSystem unitSystem = UnitSystemDemo.unitSystem();
    assertTrue(unitSystem.map().containsKey("h"));
    assertFalse(unitSystem.map().containsKey("s"));
    assertEquals(unitSystem.map().size(), 22);
  }

  @Test
  void testRotate() {
    UnitSystem unitSystem = UnitSystems.rotate(UnitSystemDemo.unitSystem(), "s", "h");
    assertTrue(unitSystem.map().containsKey("s"));
    assertFalse(unitSystem.map().containsKey("h"));
    assertEquals(unitSystem.map().size(), 22);
  }
}
