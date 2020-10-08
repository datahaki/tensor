// code by jph
package ch.ethz.idsc.tensor.qty;

import java.lang.reflect.Modifier;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class UnitMemoTest extends TestCase {
  private static void _confirmFail(String string) {
    AssertFail.of(() -> UnitMemo.CACHE.retrieve(string));
  }

  public void testLookup() {
    UnitMemo.CACHE.retrieve("A*kg^-1*s^2");
    UnitMemo.CACHE.retrieve("HaqiuytasdMAM");
    UnitMemo.CACHE.retrieve("HaqiuytasdMAM*ASsdlfkjhKJG");
    UnitMemo.CACHE.retrieve("HaqiuytasdMAM*ASsdlfkjhKJG^3");
    assertEquals(UnitMemo.CACHE.retrieve(""), Unit.ONE);
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
    assertEquals(UnitMemo.CACHE.retrieve("*a"), UnitMemo.CACHE.retrieve("a"));
    assertEquals(UnitMemo.CACHE.retrieve("a*"), UnitMemo.CACHE.retrieve("a"));
    assertEquals(UnitMemo.CACHE.retrieve("a***"), UnitMemo.CACHE.retrieve("a"));
    assertEquals(UnitMemo.CACHE.retrieve("**a***b**"), UnitMemo.CACHE.retrieve("a*b"));
  }

  public void testUnderscore() {
    Unit unit = UnitMemo.CACHE.retrieve("V_AC");
    assertTrue(unit == UnitMemo.CACHE.retrieve("V_AC"));
    assertTrue(UnitMemo.CACHE.retrieve("____").equals(UnitMemo.CACHE.retrieve("____")));
    assertFalse(UnitMemo.CACHE.retrieve("___").equals(UnitMemo.CACHE.retrieve("____")));
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
    int map_size = UnitMemo.CACHE.size();
    assertTrue(map_size <= UnitMemo.MAX_SIZE);
  }

  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(UnitMemo.class.getModifiers()));
  }
}
