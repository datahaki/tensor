// code by jph
package ch.ethz.idsc.tensor.qty;

import java.io.IOException;

import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class KnownUnitQTest extends TestCase {
  public void testKnownUnitQ() {
    assertTrue(KnownUnitQ.SI().of(Unit.of("kgf^2*K*gal^-1")));
    assertTrue(KnownUnitQ.SI().of(Unit.ONE));
  }

  public void testSimple() {
    assertTrue(KnownUnitQ.SI().of(Unit.of("V*K*CD*kOhm^-2")));
    assertTrue(KnownUnitQ.SI().of(Unit.of("PS^3")));
    assertTrue(KnownUnitQ.SI().of(Unit.of("cups")));
    assertTrue(KnownUnitQ.SI().of(Unit.of("atm^-1.5E-3")));
    assertTrue(KnownUnitQ.SI().of(Unit.of("cups*u^2")));
    assertTrue(KnownUnitQ.SI().of(Unit.of("%*%")));
  }

  public void testFalse() {
    assertFalse(KnownUnitQ.SI().of(Unit.of("CHF")));
    assertFalse(KnownUnitQ.SI().of(Unit.of("CHF*K")));
    assertFalse(KnownUnitQ.SI().of(Unit.of("CHF*m")));
    assertFalse(KnownUnitQ.SI().of(Unit.of("%%")));
  }

  public void testRequire() {
    KnownUnitQ.SI().require(Unit.of("PS^3"));
    AssertFail.of(() -> KnownUnitQ.SI().require(Unit.of("CHF")));
  }

  public void testNullCreationFail() {
    AssertFail.of(() -> KnownUnitQ.in(null));
  }

  public void testNullArgumentFail() throws ClassNotFoundException, IOException {
    KnownUnitQ knownUnitQ = Serialization.copy(KnownUnitQ.SI());
    AssertFail.of(() -> knownUnitQ.of(null));
  }
}
