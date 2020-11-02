// code by jph
package ch.ethz.idsc.tensor.qty;

import java.lang.reflect.Modifier;

import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class UnitParserTest extends TestCase {
  private static void _confirmFail(String string) {
    AssertFail.of(() -> UnitParser.of(string));
  }

  public void testLookup() {
    UnitParser.of("A*kg^-1*s^2");
    UnitParser.of("HaqiuytasdMAM");
    UnitParser.of("HaqiuytasdMAM*ASsdlfkjhKJG");
    UnitParser.of("HaqiuytasdMAM*ASsdlfkjhKJG^3");
    assertEquals(UnitParser.of(""), Unit.ONE);
  }

  public void testFail() {
    _confirmFail("Haqiuyt asdMAM");
    _confirmFail("HaqiuytasdMAM2");
    _confirmFail("Haqiuyta2sdMAM");
    _confirmFail("Haqiuyta2sdMAM^3");
    _confirmFail("2m");
    _confirmFail("m2");
    _confirmFail("m^2^2");
    _confirmFail("m*2^2");
    _confirmFail("^2");
  }

  public void testDubious() {
    assertEquals(UnitParser.of("*a"), UnitParser.of("a"));
    assertEquals(UnitParser.of("a*"), UnitParser.of("a"));
    assertEquals(UnitParser.of("a***"), UnitParser.of("a"));
    assertEquals(UnitParser.of("**a***b**"), UnitParser.of("a*b"));
  }

  public void testUnderscore() {
    Unit unit = UnitParser.of("V_AC");
    assertTrue(unit == UnitParser.of("V_AC"));
    assertTrue(UnitParser.of("____").equals(UnitParser.of("____")));
    assertFalse(UnitParser.of("___").equals(UnitParser.of("____")));
  }

  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(UnitParser.class.getModifiers()));
  }
}
