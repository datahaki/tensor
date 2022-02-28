// code by jph
package ch.alpine.tensor.qty;

import java.io.IOException;

import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class KnownUnitQTest extends TestCase {
  public void testKnownUnitQ() {
    assertTrue(KnownUnitQ.SI().test(Unit.of("kgf^2*K*gal^-1")));
    assertTrue(KnownUnitQ.SI().test(Unit.ONE));
    KnownUnitQ.SI().require(Unit.ONE);
  }

  public void testSimple() {
    assertTrue(KnownUnitQ.SI().test(Unit.of("V*K*CD*kOhm^-2")));
    assertTrue(KnownUnitQ.SI().test(Unit.of("PS^3")));
    assertTrue(KnownUnitQ.SI().test(Unit.of("cups")));
    assertTrue(KnownUnitQ.SI().test(Unit.of("atm^-1.5E-3")));
    assertTrue(KnownUnitQ.SI().test(Unit.of("cups*u^2")));
    assertTrue(KnownUnitQ.SI().test(Unit.of("%*%")));
  }

  public void testFalse() {
    assertFalse(KnownUnitQ.SI().test(Unit.of("CHF")));
    assertFalse(KnownUnitQ.SI().test(Unit.of("CHF*K")));
    assertFalse(KnownUnitQ.SI().test(Unit.of("CHF*m")));
    assertFalse(KnownUnitQ.SI().test(Unit.of("%%")));
  }

  public void testAll() {
    KnownUnitQ knownUnitQ = KnownUnitQ.SI();
    knownUnitQ.require(Unit.of("cd"));
    knownUnitQ.require(Unit.of("m"));
    knownUnitQ.require(Unit.of("kg"));
    knownUnitQ.require(Unit.of("K"));
    knownUnitQ.require(Unit.of("CD"));
    knownUnitQ.require(Unit.of("V"));
    assertFalse(knownUnitQ.test(Unit.of("CHF")));
    assertFalse(knownUnitQ.test(Unit.of("EUR")));
    assertFalse(knownUnitQ.test(Unit.of("USD")));
  }

  public void testCurrencies() {
    UnitSystem unitSystem = SimpleUnitSystem.from(ResourceData.properties("/unit/chf.properties"));
    KnownUnitQ knownUnitQ = KnownUnitQ.in(unitSystem);
    knownUnitQ.require(Unit.of("CHF"));
    knownUnitQ.require(Unit.of("AUD"));
    assertFalse(knownUnitQ.test(Unit.of("m")));
  }

  public void testToString() {
    assertTrue(KnownUnitQ.SI().toString().startsWith("KnownUnitQ["));
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
    AssertFail.of(() -> knownUnitQ.test(null));
    AssertFail.of(() -> knownUnitQ.require(null));
  }
}
