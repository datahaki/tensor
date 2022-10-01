// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.ext.MergeIllegal;
import ch.alpine.tensor.num.GaussScalar;

class UnitTest {
  public static Scalar requireNonZero(Scalar scalar) {
    if (scalar instanceof Quantity || //
        Scalars.isZero(scalar))
      throw new Throw(scalar);
    return scalar;
  }

  private static final Collector<Entry<String, Scalar>, ?, NavigableMap<String, Scalar>> COLLECTOR = //
      Collectors.toMap( //
          entry -> UnitParser.requireAtomic(entry.getKey()), //
          entry -> requireNonZero(entry.getValue()), //
          MergeIllegal.operator(), TreeMap::new);

  /** @param map
   * @return */
  public static Unit unit(Map<String, Scalar> map) {
    return UnitImpl.create(map.entrySet().stream().collect(COLLECTOR));
  }

  @Test
  void testString() {
    String check = "m*s^3";
    Unit unit = Unit.of(check);
    assertEquals(unit.toString(), check);
  }

  @Test
  void testSpaces() {
    assertEquals(Unit.of(" m ").toString(), "m");
    assertEquals(Unit.of(" m ^ 3 ").toString(), "m^3");
    assertEquals(Unit.of(" m ^ 3 * rad ").toString(), "m^3*rad");
    assertEquals(Unit.of(""), Unit.ONE);
    assertEquals(Unit.of(" "), Unit.ONE);
    assertEquals(Unit.of("  "), Unit.ONE);
  }

  @ParameterizedTest
  @ValueSource(strings = { "*", " * ", "**", "* * ", "  **  * " })
  void testSeparators(String string) {
    assertEquals(Unit.of(string), Unit.ONE);
  }

  @Test
  void testEqualsHash() {
    Unit kg1 = Unit.of("kg");
    Unit kg2 = Unit.of("kg*m");
    Unit m = Unit.of("m");
    assertEquals(kg1, kg2.add(m.negate()));
    assertEquals(kg1.hashCode(), kg2.add(m.negate()).hashCode());
    assertFalse(kg1.equals(m));
    assertFalse(kg1.equals(new Object()));
  }

  @Test
  void testMultiplyZero() {
    Unit unit = Unit.of("kg");
    Unit gone = unit.multiply(RealScalar.ZERO);
    assertTrue(UnitQ.isOne(gone));
  }

  @Test
  void testMultiplyZero2() {
    Unit unit = Unit.of("kg*m^-3");
    Unit gone = unit.multiply(RealScalar.ZERO);
    assertTrue(UnitQ.isOne(gone));
  }

  @Test
  void testMultiplyFail() {
    Unit kg1 = Unit.of("kg");
    Scalar q = Quantity.of(3, "m");
    assertThrows(Throw.class, () -> kg1.multiply(q));
  }

  @Test
  void testOneString() {
    assertEquals(Unit.ONE.toString(), "");
    assertTrue(Unit.ONE.map().isEmpty());
  }

  @Test
  void testGaussScalar() {
    Map<String, Scalar> map = new HashMap<>();
    map.put("some", GaussScalar.of(1, 7));
    unit(map);
    map.put("zero", GaussScalar.of(0, 7));
    assertThrows(Throw.class, () -> unit(map));
  }

  @Test
  void testQuantityExponentFail() {
    Map<String, Scalar> map = new HashMap<>();
    map.put("some", Quantity.of(1, "r"));
    assertThrows(Throw.class, () -> unit(map));
  }

  // https://tinyurl.com/y44sj2et
  @Test
  void testRational() {
    Unit uExact = Unit.of("m^6*bar*mol^-2*K^1/2");
    Unit uNumer = Unit.of("m^6*bar*mol^-2*K^0.5");
    assertEquals(uNumer, uExact);
    Unit uBrack = Unit.of("m^6*bar*mol^-2*K^(1/2)");
    assertEquals(uExact, uBrack);
  }

  @Test
  void testReference() {
    assertSame(Unit.of("m*s*"), Unit.of("s*m"));
    assertSame(Unit.of("m^2*s*"), Unit.of("s*m*m"));
  }

  @Test
  void testKeyCollision() {
    Map<String, Scalar> map1 = new HashMap<>();
    map1.put("a", RealScalar.ONE);
    map1.put("b", RealScalar.ONE.negate());
    Map<String, Scalar> map2 = new HashMap<>();
    map2.put("a", RealScalar.TWO);
    assertThrows(IllegalStateException.class, () -> Stream.concat(map1.entrySet().stream(), map2.entrySet().stream()).collect(COLLECTOR));
  }

  @ParameterizedTest
  @ValueSource(strings = { " m >", "| m ", "|", "^", "unknown-seeManual", "a+b", "b=c" })
  void testFail(String string) {
    assertThrows(IllegalArgumentException.class, () -> Unit.of(string));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> Unit.of((String) null));
    assertThrows(NullPointerException.class, () -> unit((Map<String, Scalar>) null));
  }
}
