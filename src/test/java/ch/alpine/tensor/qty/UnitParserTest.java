// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

class UnitParserTest {
  private static void _confirmFail(String string) {
    assertThrows(Exception.class, () -> UnitParser.of(string));
  }

  @Test
  void testLookup() {
    UnitParser.of("A*kg^-1*s^2");
    UnitParser.of("HaqiuytasdMAM");
    UnitParser.of("HaqiuytasdMAM*ASsdlfkjhKJG");
    UnitParser.of("HaqiuytasdMAM*ASsdlfkjhKJG^3");
    assertEquals(UnitParser.of(""), Unit.ONE);
  }

  @Test
  void testFail() {
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

  @Test
  void testDubious() {
    assertEquals(UnitParser.of("*a"), UnitParser.of("a"));
    assertEquals(UnitParser.of("a*"), UnitParser.of("a"));
    assertEquals(UnitParser.of("a***"), UnitParser.of("a"));
    assertEquals(UnitParser.of("**a***b**"), UnitParser.of("a*b"));
  }

  @Test
  void testUnderscore() {
    Unit unit = UnitParser.of("V_AC");
    assertTrue(unit == UnitParser.of("V_AC"));
    assertTrue(UnitParser.of("____").equals(UnitParser.of("____")));
    assertFalse(UnitParser.of("___").equals(UnitParser.of("____")));
  }

  @Test
  void testPackageVisibility() {
    assertFalse(Modifier.isPublic(UnitParser.class.getModifiers()));
  }
}
