// code by jph
package ch.alpine.tensor.qty;

import java.util.Arrays;
import java.util.Set;

import ch.alpine.tensor.io.ResourceData;
import junit.framework.TestCase;

public class UnitSystemInflatorTest extends TestCase {
  private final UnitSystemInflator unitSystemInflator = //
      new UnitSystemInflator(StaticHelper.stringScalarMap(ResourceData.properties("/unit/si.properties")));

  public void testAtoms() {
    Set<String> atoms = unitSystemInflator.atoms();
    assertTrue(atoms.containsAll(Arrays.asList("cd A B s K mol kg m".split(" "))));
    assertEquals(atoms.size(), 8);
  }

  public void testSkipped() {
    Set<String> skipped = unitSystemInflator.skipped();
    assertTrue(skipped.containsAll(Arrays.asList("PS pt ft".split(" "))));
    assertEquals(skipped.size(), 3);
  }

  public void testKg() {
    assertEquals(UnitSystem.SI().apply(Quantity.of(1, "kg")), Quantity.of(1, "kg"));
    assertEquals(UnitSystem.SI().apply(Quantity.of(1, "g")), Quantity.of(1e-3, "kg"));
  }

  public void testHz() {
    assertEquals(UnitSystem.SI().apply(Quantity.of(1, "Hz")), Quantity.of(1, "s^-1"));
    assertEquals(UnitSystem.SI().apply(Quantity.of(1, "kHz")), Quantity.of(1000, "s^-1"));
  }

  public void testSmall() {
    UnitSystemInflator unitSystemInflator = //
        new UnitSystemInflator(StaticHelper.stringScalarMap(ResourceData.properties("/unit/small.properties")));
    Set<String> atoms = unitSystemInflator.atoms();
    assertTrue(atoms.containsAll(Arrays.asList("cd A s K mol kg m".split(" "))));
    assertEquals(atoms.size(), 7);
  }

  public void testSmallUS() {
    UnitSystem unitSystem = UnitSystemInflator.of(ResourceData.properties("/unit/small.properties"));
    assertEquals(unitSystem.apply(Quantity.of(12, "K")), Quantity.of(12, "K"));
    assertTrue(KnownUnitQ.in(unitSystem).test(Unit.of("K")));
  }
}
