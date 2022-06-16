// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.pow.Power;

class MathematicaFormTest {
  @Test
  void testSimple() {
    String string = MathematicaForm.of(Quantity.of(3, "m^2*s"));
    assertEquals(string, "Quantity[3, \"Meters\"^2*\"Seconds\"]");
  }

  @Test
  void testSimple2() {
    String string = MathematicaForm.of(Tensors.of(Quantity.of(3, "A^2*V^-1")));
    assertEquals(string, "{Quantity[3, \"Amperes\"^2*\"Volts\"^-1]}");
  }

  @Test
  void testKeys() {
    for (String string : MathematicaForm.INSTANCE.getMap().keySet()) {
      boolean test = KnownUnitQ.SI().test(Unit.of(string));
      if (!test)
        System.err.println(string);
      // assertTrue(KnownUnitQ.SI().test(Unit.of(string)));
    }
  }

  @Test
  void testSpecific() {
    Map<String, String> map = MathematicaForm.INSTANCE.getMap();
    assertTrue(map.containsKey("kV"));
    assertTrue(map.containsKey("dN"));
    assertTrue(map.containsKey("dcd"));
  }

  @Test
  void testUnits() {
    Map<String, String> map = MathematicaForm.INSTANCE.getMap();
    for (String string : UnitSystem.SI().map().keySet())
      if (!map.containsKey(string))
        throw new IllegalArgumentException(string);
  }

  @Test
  void testNamesUppercase() {
    for (String string : MathematicaForm.INSTANCE.getMap().values()) {
      String u = "" + string.charAt(0);
      assertEquals(u, u.toUpperCase());
    }
  }

  @Test
  void testXibi() {
    assertEquals(QuantityMagnitude.SI().in("B").apply(Quantity.of(1, "KiB")), Power.of(1024, 1));
    assertEquals(QuantityMagnitude.SI().in("B").apply(Quantity.of(1, "MiB")), Power.of(1024, 2));
    assertEquals(QuantityMagnitude.SI().in("B").apply(Quantity.of(1, "GiB")), Power.of(1024, 3));
    assertEquals(QuantityMagnitude.SI().in("B").apply(Quantity.of(1, "TiB")), Power.of(1024, 4));
    assertEquals(QuantityMagnitude.SI().in("B").apply(Quantity.of(1, "PiB")), Power.of(1024, 5));
  }
}
