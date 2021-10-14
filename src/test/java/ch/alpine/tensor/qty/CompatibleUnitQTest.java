// code by jph
package ch.alpine.tensor.qty;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class CompatibleUnitQTest extends TestCase {
  public void testCompatibleUnitQ() {
    assertTrue(CompatibleUnitQ.SI().with(Unit.of("kgf^2*L^-3")).test(Quantity.of(2, "N^2*m^-9")));
    assertTrue(CompatibleUnitQ.SI().with(Unit.of("cups")).test(Quantity.of(2, "L")));
    assertTrue(CompatibleUnitQ.SI().with(Unit.of("m^2*kg*s^-3")).test(Quantity.of(3, "W")));
    assertTrue(CompatibleUnitQ.SI().with(Unit.of("ksi")).test(Quantity.of(3, "atm")));
    assertFalse(CompatibleUnitQ.SI().with(Unit.of("ksir")).test(Quantity.of(3, "atm")));
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    Predicate<Scalar> predicate = Serialization.copy(CompatibleUnitQ.SI().with(Unit.of("N*s")));
    String string = predicate.toString();
    assertTrue(string.startsWith("CompatibleUnitQ["));
    assertTrue(string.contains("*s"));
  }

  public void testSimple() throws ClassNotFoundException, IOException {
    CompatibleUnitQ compatibleUnitQ = Serialization.copy(CompatibleUnitQ.SI());
    assertTrue(compatibleUnitQ.with(Unit.of("m*s^-1")).test(Quantity.of(2, "km*ms^-1")));
    assertTrue(CompatibleUnitQ.SI().with(Unit.of("PS^2")).test(Quantity.of(2, "W^2")));
    assertFalse(CompatibleUnitQ.SI().with("m*s^-1").test(Quantity.of(2, "m*s")));
    assertFalse(CompatibleUnitQ.SI().with("s").test(Quantity.of(1, "Hz")));
  }

  public void testAssignable() {
    assertFalse(Quantity.class.isAssignableFrom(RealScalar.ONE.getClass()));
    assertTrue(Quantity.class.isAssignableFrom(Quantity.of(1, "s").getClass()));
  }

  public void testOne() {
    GaussScalar s = GaussScalar.of(2, 13);
    assertTrue(CompatibleUnitQ.SI().with(Unit.ONE).test(s));
    assertTrue(CompatibleUnitQ.SI().with("").test(s));
  }

  public void testNonReal() {
    GaussScalar s = GaussScalar.of(2, 13);
    Scalar q1 = Quantity.of(s, "fiction");
    Scalar q2 = Quantity.of(GaussScalar.of(3, 13), "end^2");
    Map<String, Scalar> map = new TreeMap<>();
    map.put("fiction", q2);
    UnitSystem unitSystem = SimpleUnitSystem.from(map);
    assertTrue(CompatibleUnitQ.in(unitSystem).with(Unit.of("fiction")).test(q1));
    assertTrue(CompatibleUnitQ.in(unitSystem).with(Unit.of("fiction")).test(q2));
    assertEquals(new QuantityMagnitude(unitSystem).in("end^2").apply(q1), GaussScalar.of(6, 13));
    assertEquals(new QuantityMagnitude(unitSystem).in("end^2").apply(q2), GaussScalar.of(3, 13));
    Scalar p12 = q1.multiply(q2);
    Scalar s12 = unitSystem.apply(p12);
    Scalar q3 = Quantity.of(GaussScalar.of(5, 13), "end^4");
    assertEquals(s12, q3);
    Scalar g1 = GaussScalar.of(2, 13);
    assertEquals(new QuantityMagnitude(unitSystem).in("fiction^2").apply(q3), g1);
    Scalar s1 = unitSystem.apply(Quantity.of(g1, "fiction^2"));
    assertEquals(s1, q3);
  }

  public void testTime() {
    Predicate<Scalar> predicate = CompatibleUnitQ.SI().with(Unit.of("h"));
    assertTrue(predicate.test(Quantity.of(0, "K^0*Hz^-1")));
    assertTrue(predicate.test(Quantity.of(1, "F*S^-1")));
    assertTrue(predicate.test(Quantity.of(2, "H*Ohm^-1")));
    assertTrue(predicate.test(Quantity.of(3, "Wb*V^-1")));
    assertTrue(predicate.test(Quantity.of(4, "mol*kat^-1")));
    assertTrue(predicate.test(Quantity.of(5, "C*A^-1")));
    assertTrue(predicate.test(Quantity.of(6, "J*W^-1")));
    assertTrue(predicate.test(Quantity.of(7, "T*cd*V^-1*lux^-1")));
  }

  public void testWithFail() {
    AssertFail.of(() -> CompatibleUnitQ.SI().with((Unit) null));
    AssertFail.of(() -> CompatibleUnitQ.SI().with((String) null));
  }

  public void testInNullFail() {
    AssertFail.of(() -> CompatibleUnitQ.in(null));
  }
}
