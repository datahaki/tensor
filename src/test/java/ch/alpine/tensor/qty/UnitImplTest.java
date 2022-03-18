// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Collections;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.usr.AssertFail;

public class UnitImplTest {
  @Test
  public void testMap() {
    Unit unit = Unit.of("kg^2*m^-1");
    assertEquals(unit.map().get("kg"), RealScalar.of(2));
    assertEquals(unit.map().get("m"), RealScalar.of(-1));
    assertFalse(unit.map().containsKey("A"));
  }

  @Test
  public void testMap2() {
    Unit unit = Unit.of("kg^2*m^-1*s");
    assertEquals(unit.map().get("kg"), RealScalar.of(2));
    assertEquals(unit.map().get("m"), RealScalar.of(-1));
    assertEquals(unit.map().get("s"), RealScalar.ONE);
    assertFalse(unit.map().containsKey("A"));
  }

  @Test
  public void testMultiplyFail() {
    Unit unit = Unit.of("kg^2*m^-1");
    AssertFail.of(() -> unit.multiply(Quantity.of(3, "s")));
  }

  @Test
  public void testSerializationEquals() throws ClassNotFoundException, IOException {
    Unit unit1 = Unit.of("kg^2*m^-1*K*ABC");
    Unit unit2 = Serialization.copy(unit1);
    assertEquals(unit1, unit2);
    assertFalse(unit1 == unit2);
    Unit unit1_negate = unit1.negate();
    Unit unit2_negate = unit2.negate();
    assertTrue(unit1_negate == unit2_negate);
  }

  @Test
  public void testCachedEquals() {
    Unit unit1 = Unit.of("kg^2/3*m^-3*K");
    Unit unit2 = Unit.of("kg^2/3*m^-3*K");
    assertTrue(unit1 == unit2);
    Unit unit1_negate = unit1.negate();
    Unit unit2_negate = unit2.negate();
    assertTrue(unit1_negate == unit2_negate);
  }

  @Test
  public void testUnmodifiableMap() {
    Unit unit = Unit.of("kg^2*m^-1");
    AssertFail.of(() -> unit.map().clear());
  }

  @Test
  public void testReference1() {
    NavigableMap<String, Scalar> m1 = new TreeMap<>();
    m1.put("some", RealScalar.ONE);
    m1.put("kgt", RealScalar.TWO.negate());
    NavigableMap<String, Scalar> m2 = new TreeMap<>();
    m2.put("kgt", RealScalar.of(2).negate());
    m2.put("some", RealScalar.of(1));
    assertTrue(UnitImpl.create(m1) == UnitImpl.create(m2));
  }

  @Test
  public void testReference2() {
    assertTrue(UnitImpl.create(Collections.emptyNavigableMap()) == //
        UnitImpl.create(Collections.emptyNavigableMap()));
  }

  @Test
  public void testEqualsMerged() {
    Unit unit = Unit.of("m^2*m^-1*kg*m^1");
    assertEquals(unit, Unit.of("kg****m ** m*  "));
    assertEquals(unit.toString(), "kg*m^2");
  }

  @Test
  public void testEqualsNull() {
    assertFalse(Unit.ONE.equals(null));
    assertFalse(Unit.of("m").equals(null));
  }
}
