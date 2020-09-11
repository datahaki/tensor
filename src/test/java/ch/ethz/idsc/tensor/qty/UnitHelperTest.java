// code by jph
package ch.ethz.idsc.tensor.qty;

import junit.framework.TestCase;

public class UnitHelperTest extends TestCase {
  private static void _confirmFail(String string) {
    try {
      UnitHelper.MEMO.lookup(string);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testLookup() {
    UnitHelper.MEMO.lookup("A*kg^-1*s^2");
    UnitHelper.MEMO.lookup("HaqiuytasdMAM");
    UnitHelper.MEMO.lookup("HaqiuytasdMAM*ASsdlfkjhKJG");
    UnitHelper.MEMO.lookup("HaqiuytasdMAM*ASsdlfkjhKJG^3");
    assertEquals(UnitHelper.MEMO.lookup(""), Unit.ONE);
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
    assertEquals(UnitHelper.MEMO.lookup("*a"), UnitHelper.MEMO.lookup("a"));
    assertEquals(UnitHelper.MEMO.lookup("a*"), UnitHelper.MEMO.lookup("a"));
    assertEquals(UnitHelper.MEMO.lookup("a***"), UnitHelper.MEMO.lookup("a"));
    assertEquals(UnitHelper.MEMO.lookup("**a***b**"), UnitHelper.MEMO.lookup("a*b"));
  }

  public void testUnderscore() {
    Unit unit = UnitHelper.MEMO.lookup("V_AC");
    assertTrue(unit == UnitHelper.MEMO.lookup("V_AC"));
    assertTrue(UnitHelper.MEMO.lookup("____").equals(UnitHelper.MEMO.lookup("____")));
    assertFalse(UnitHelper.MEMO.lookup("___").equals(UnitHelper.MEMO.lookup("____")));
  }

  public void testMap() {
    for (int c1 = 0; c1 < 26; ++c1) {
      char chr1 = (char) (65 + c1);
      for (int c2 = 0; c2 < 26; ++c2) {
        char chr2 = (char) (65 + c2);
        for (int c3 = 0; c3 < 13; ++c3) {
          char chr3 = (char) (65 + c3);
          Unit.of(chr1 + "" + chr2 + "" + chr3);
        }
      }
    }
    int map_size = UnitHelper.MEMO.map_size();
    assertTrue(map_size < 1000);
  }
}
