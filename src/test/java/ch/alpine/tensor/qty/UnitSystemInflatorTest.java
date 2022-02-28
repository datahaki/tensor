// code by jph
package ch.alpine.tensor.qty;

import java.util.Arrays;
import java.util.Set;

import ch.alpine.tensor.io.ResourceData;
import junit.framework.TestCase;

public class UnitSystemInflatorTest extends TestCase {
  public void testSimple() {
    new UnitSystemInflator(ResourceData.properties("/unit/si.properties")).getMap();
  }

  public void testAtoms() {
    Set<String> atoms = new UnitSystemInflator(ResourceData.properties("/unit/si.properties")).atoms();
    assertTrue(atoms.containsAll(Arrays.asList("cd A B s K mol kg m".split(" "))));
    assertEquals(atoms.size(), 8);
  }

  public void testSkipped() {
    Set<String> skipped = new UnitSystemInflator(ResourceData.properties("/unit/si.properties")).skipped();
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
}
