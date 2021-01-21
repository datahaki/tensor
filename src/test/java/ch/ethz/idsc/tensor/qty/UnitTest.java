// code by jph
package ch.ethz.idsc.tensor.qty;

import java.util.HashMap;
import java.util.Map;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.num.GaussScalar;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class UnitTest extends TestCase {
  public void testString() {
    String check = "m*s^3";
    Unit unit = Unit.of(check);
    assertEquals(unit.toString(), check);
  }

  public void testSpaces() {
    assertEquals(Unit.of(" m ").toString(), "m");
    assertEquals(Unit.of(" m ^ 3 ").toString(), "m^3");
    assertEquals(Unit.of(" m ^ 3 * rad ").toString(), "m^3*rad");
    assertEquals(Unit.of(""), Unit.ONE);
    assertEquals(Unit.of(" "), Unit.ONE);
    assertEquals(Unit.of("  "), Unit.ONE);
  }

  public void testSeparators() {
    assertEquals(Unit.of("*"), Unit.ONE);
    assertEquals(Unit.of(" * "), Unit.ONE);
    assertEquals(Unit.of("**"), Unit.ONE);
    assertEquals(Unit.of("* * "), Unit.ONE);
    assertEquals(Unit.of("  **  * "), Unit.ONE);
  }

  public void testEqualsHash() {
    Unit kg1 = Unit.of("kg");
    Unit kg2 = Unit.of("kg*m");
    Unit m = Unit.of("m");
    assertEquals(kg1, kg2.add(m.negate()));
    assertEquals(kg1.hashCode(), kg2.add(m.negate()).hashCode());
    assertFalse(kg1.equals(m));
    assertFalse(kg1.equals(new Object()));
  }

  public void testMultiplyZero() {
    Unit unit = Unit.of("kg");
    Unit gone = unit.multiply(RealScalar.ZERO);
    assertTrue(UnitQ.isOne(gone));
  }

  public void testMultiplyZero2() {
    Unit unit = Unit.of("kg*m^-3");
    Unit gone = unit.multiply(RealScalar.ZERO);
    assertTrue(UnitQ.isOne(gone));
  }

  public void testMultiplyFail() {
    Unit kg1 = Unit.of("kg");
    Scalar q = Quantity.of(3, "m");
    AssertFail.of(() -> kg1.multiply(q));
  }

  public void testOneString() {
    assertEquals(Unit.ONE.toString(), "");
    assertTrue(Unit.ONE.map().isEmpty());
  }

  public void testGaussScalar() {
    Map<String, Scalar> map = new HashMap<>();
    map.put("some", GaussScalar.of(1, 7));
    UnitSystems.unit(map);
    map.put("some", GaussScalar.of(0, 7));
    AssertFail.of(() -> UnitSystems.unit(map));
  }

  public void testFail() {
    AssertFail.of(() -> Unit.of(" m >"));
    AssertFail.of(() -> Unit.of("| m "));
    AssertFail.of(() -> Unit.of("|"));
    AssertFail.of(() -> Unit.of("^"));
    AssertFail.of(() -> Unit.of("unknown-seeManual"));
    AssertFail.of(() -> Unit.of("a+b"));
    AssertFail.of(() -> Unit.of("b=c"));
  }

  public void testNullFail() {
    AssertFail.of(() -> Unit.of((String) null));
    AssertFail.of(() -> UnitSystems.unit((Map<String, Scalar>) null));
  }
}
