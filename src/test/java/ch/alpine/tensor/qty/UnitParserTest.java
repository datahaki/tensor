// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

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

  @ParameterizedTest
  @CsvFileSource(resources = "/ch/alpine/tensor/qty/unitmap.csv")
  void testCsv(String input, String expect) {
    assertEquals(UnitParser.of(input), UnitParser.of(expect));
  }

  @Test
  void testUnderscore() {
    Unit unit = UnitParser.of("V_AC");
    assertSame(unit, UnitParser.of("V_AC"));
    assertEquals(UnitParser.of("____"), UnitParser.of("____"));
    assertNotEquals(UnitParser.of("___"), UnitParser.of("____"));
  }

  @Test
  void testPackageVisibility() {
    assertFalse(Modifier.isPublic(UnitParser.class.getModifiers()));
  }
}
