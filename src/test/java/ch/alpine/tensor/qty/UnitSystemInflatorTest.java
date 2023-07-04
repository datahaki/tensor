// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Set;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ext.ResourceData;

class UnitSystemInflatorTest {
  private final UnitSystemInflator unitSystemInflator = //
      new UnitSystemInflator(StaticHelper.stringScalarMap(ResourceData.properties("/ch/alpine/tensor/qty/si.properties")));

  @Test
  void testAtoms() {
    Set<String> atoms = unitSystemInflator.atoms();
    assertTrue(atoms.containsAll(Arrays.asList("cd A B s K mol kg m".split(" "))));
    assertEquals(atoms.size(), 8);
  }

  @Test
  void testSkipped() {
    Set<String> skipped = unitSystemInflator.skipped();
    assertTrue(skipped.containsAll(Arrays.asList("PS pt ft".split(" "))));
    assertEquals(skipped.size(), 3);
  }

  @Test
  void testKg() {
    assertEquals(UnitSystem.SI().apply(Quantity.of(1, "kg")), Quantity.of(1, "kg"));
    assertEquals(UnitSystem.SI().apply(Quantity.of(1, "g")), Quantity.of(1e-3, "kg"));
  }

  @Test
  void testHz() {
    assertEquals(UnitSystem.SI().apply(Quantity.of(1, "Hz")), Quantity.of(1, "s^-1"));
    assertEquals(UnitSystem.SI().apply(Quantity.of(1, "kHz")), Quantity.of(1000, "s^-1"));
  }

  @Test
  void testSmall() {
    UnitSystemInflator unitSystemInflator = //
        new UnitSystemInflator(StaticHelper.stringScalarMap(ResourceData.properties("/ch/alpine/tensor/qty/small.properties")));
    Set<String> atoms = unitSystemInflator.atoms();
    assertTrue(atoms.containsAll(Arrays.asList("cd A s K mol kg m".split(" "))));
    assertEquals(atoms.size(), 7);
  }

  @Test
  void testSmallUS() {
    UnitSystem unitSystem = UnitSystemInflator.of(ResourceData.properties("/ch/alpine/tensor/qty/small.properties"));
    assertEquals(unitSystem.apply(Quantity.of(12, "K")), Quantity.of(12, "K"));
    assertTrue(KnownUnitQ.in(unitSystem).test(Unit.of("K")));
  }
}
