// code by jph
package ch.ethz.idsc.tensor.qty;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class SimpleUnitSystemTest extends TestCase {
  public void testMap() throws ClassNotFoundException, IOException {
    Properties properties = new Properties();
    properties.setProperty("cent", "1/100[FRA]");
    UnitSystem unitSystem = Serialization.copy(SimpleUnitSystem.from(properties));
    Scalar scalar = unitSystem.apply(Quantity.of(100, "cent"));
    assertEquals(scalar, Quantity.of(1, "FRA"));
    assertEquals(unitSystem.map().size(), 1);
  }

  public void testAll() {
    UnitSystem unitSystem = UnitSystem.SI();
    assertTrue(unitSystem instanceof SimpleUnitSystem);
    Set<String> set = unitSystem.units();
    assertTrue(set.contains("cd"));
    assertTrue(set.contains("m"));
    assertTrue(set.contains("kg"));
    assertTrue(set.contains("K"));
    assertTrue(set.contains("CD"));
    assertTrue(set.contains("V"));
    assertFalse(set.contains("CHF"));
    assertFalse(set.contains("EUR"));
    assertFalse(set.contains("USD"));
  }

  public void testFailKey1() {
    Properties properties = new Properties();
    properties.setProperty("cent123", "1/100[FRA]");
    AssertFail.of(() -> SimpleUnitSystem.from(properties));
  }

  public void testFailKey2() {
    Properties properties = new Properties();
    properties.setProperty(" cent", "1/100[FRA]");
    AssertFail.of(() -> SimpleUnitSystem.from(properties));
  }

  public void testFailValue1() {
    Properties properties = new Properties();
    properties.setProperty("cent", "1/100a[FRA]");
    AssertFail.of(() -> SimpleUnitSystem.from(properties));
  }

  public void testFailValue2() {
    Properties properties = new Properties();
    properties.setProperty("cent", "b/100a");
    AssertFail.of(() -> SimpleUnitSystem.from(properties));
  }

  public void testDerive() throws ClassNotFoundException, IOException {
    UnitSystem unitSystem = Serialization.copy(SimpleUnitSystem.from(UnitSystem.SI().map()));
    assertEquals(unitSystem.map(), UnitSystem.SI().map());
  }

  public void testEmpty() {
    UnitSystem unitSystem = SimpleUnitSystem.from(new Properties());
    assertTrue(unitSystem.map().isEmpty());
  }

  public void testFailNullProperties() {
    AssertFail.of(() -> SimpleUnitSystem.from((Properties) null));
  }

  public void testFailNullMap() {
    AssertFail.of(() -> SimpleUnitSystem.from((Map<String, Scalar>) null));
  }
}
