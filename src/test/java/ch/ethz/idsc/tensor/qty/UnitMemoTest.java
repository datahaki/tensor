// code by jph
package ch.ethz.idsc.tensor.qty;

import java.lang.reflect.Modifier;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.ext.Cache;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class UnitMemoTest extends TestCase {
  private static void _confirmFail(String string) {
    AssertFail.of(() -> UnitMemo.of(string));
  }

  public void testLookup() {
    UnitMemo.of("A*kg^-1*s^2");
    UnitMemo.of("HaqiuytasdMAM");
    UnitMemo.of("HaqiuytasdMAM*ASsdlfkjhKJG");
    UnitMemo.of("HaqiuytasdMAM*ASsdlfkjhKJG^3");
    assertEquals(UnitMemo.of(""), Unit.ONE);
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
    assertEquals(UnitMemo.of("*a"), UnitMemo.of("a"));
    assertEquals(UnitMemo.of("a*"), UnitMemo.of("a"));
    assertEquals(UnitMemo.of("a***"), UnitMemo.of("a"));
    assertEquals(UnitMemo.of("**a***b**"), UnitMemo.of("a*b"));
  }

  public void testUnderscore() {
    Unit unit = UnitMemo.of("V_AC");
    assertTrue(unit == UnitMemo.of("V_AC"));
    assertTrue(UnitMemo.of("____").equals(UnitMemo.of("____")));
    assertFalse(UnitMemo.of("___").equals(UnitMemo.of("____")));
  }

  public void testMap() {
    IntStream.range(0, 26).parallel().forEach(c1 -> {
      char chr1 = (char) (65 + c1);
      for (int c2 = 0; c2 < 26; ++c2) {
        char chr2 = (char) (65 + c2);
        for (int c3 = 0; c3 < 13; ++c3) {
          char chr3 = (char) (65 + c3);
          Unit.of(chr1 + "" + chr2 + "" + chr3);
        }
      }
    });
    Cache<String, Unit> cache = (Cache<String, Unit>) UnitMemo.CACHE;
    int map_size = cache.size();
    assertTrue(map_size == UnitMemo.MAX_SIZE);
  }

  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(UnitMemo.class.getModifiers()));
  }
}
