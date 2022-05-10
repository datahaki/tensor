// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.io.ResourceData;

class KnownUnitQTest {
  @Test
  public void testKnownUnitQ() {
    assertTrue(KnownUnitQ.SI().test(Unit.of("kgf^2*K*gal^-1")));
    assertTrue(KnownUnitQ.SI().test(Unit.ONE));
    KnownUnitQ.SI().require(Unit.ONE);
  }

  @Test
  public void testSimple() {
    assertTrue(KnownUnitQ.SI().test(Unit.of("V*K*CD*kOhm^-2")));
    assertTrue(KnownUnitQ.SI().test(Unit.of("PS^3")));
    assertTrue(KnownUnitQ.SI().test(Unit.of("cup"))); // Cups
    assertTrue(KnownUnitQ.SI().test(Unit.of("atm^-1.5E-3")));
    assertTrue(KnownUnitQ.SI().test(Unit.of("cup*u^2")));
    assertTrue(KnownUnitQ.SI().test(Unit.of("%*%")));
  }

  @Test
  public void testFalse() {
    assertFalse(KnownUnitQ.SI().test(Unit.of("CHF")));
    assertFalse(KnownUnitQ.SI().test(Unit.of("CHF*K")));
    assertFalse(KnownUnitQ.SI().test(Unit.of("CHF*m")));
    assertFalse(KnownUnitQ.SI().test(Unit.of("%%")));
  }

  @Test
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

  @Test
  public void testCurrencies() {
    UnitSystem unitSystem = SimpleUnitSystem.from(ResourceData.properties("/unit/chf.properties"));
    KnownUnitQ knownUnitQ = KnownUnitQ.in(unitSystem);
    knownUnitQ.require(Unit.of("CHF"));
    knownUnitQ.require(Unit.of("AUD"));
    assertFalse(knownUnitQ.test(Unit.of("m")));
  }

  @Test
  public void testToString() {
    assertTrue(KnownUnitQ.SI().toString().startsWith("KnownUnitQ["));
  }

  @Test
  public void testRequire() {
    KnownUnitQ.SI().require(Unit.of("PS^3"));
    assertThrows(IllegalArgumentException.class, () -> KnownUnitQ.SI().require(Unit.of("CHF")));
  }

  @Test
  public void testNullCreationFail() {
    assertThrows(NullPointerException.class, () -> KnownUnitQ.in(null));
  }

  @Test
  public void testNullArgumentFail() throws ClassNotFoundException, IOException {
    KnownUnitQ knownUnitQ = Serialization.copy(KnownUnitQ.SI());
    assertThrows(NullPointerException.class, () -> knownUnitQ.test(null));
    assertThrows(NullPointerException.class, () -> knownUnitQ.require(null));
  }
}
