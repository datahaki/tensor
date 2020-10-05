// code by jph
package ch.ethz.idsc.tensor.qty;

import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class UnitQTest extends TestCase {
  public void testZero() {
    Unit unit = Unit.of("m^0*s^-0");
    assertTrue(UnitQ.isOne(unit));
  }

  public void testDouble() {
    assertEquals(Unit.of("m*m^3"), Unit.of("m*m^2*m"));
    assertTrue(UnitQ.isOne(Unit.of("m*m^-1")));
    assertTrue(UnitQ.isOne(Unit.of("s^2*m*s^-1*m^-1*s^-1")));
  }

  public void testEmpty() {
    assertTrue(UnitQ.isOne(Unit.of("")));
    assertTrue(UnitQ.isOne(Unit.ONE));
  }

  public void testFail() {
    AssertFail.of(() -> UnitQ.isOne(null));
  }
}
