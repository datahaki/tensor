// code by jph
package ch.ethz.idsc.tensor.qty;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.num.GaussScalar;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class CompatibleUnitQTest extends TestCase {
  public void testCompatibleUnitQ() {
    assertTrue(CompatibleUnitQ.SI().with(Unit.of("kgf^2*L^-3")).test(Quantity.of(2, "N^2*m^-9")));
    assertTrue(CompatibleUnitQ.SI().with(Unit.of("cups")).test(Quantity.of(2, "L")));
    assertTrue(CompatibleUnitQ.SI().with(Unit.of("m^2*kg*s^-3")).test(Quantity.of(3, "W")));
  }

  public void testSimple() throws ClassNotFoundException, IOException {
    CompatibleUnitQ compatibleUnitQ = Serialization.copy(CompatibleUnitQ.SI());
    assertTrue(compatibleUnitQ.with(Unit.of("m*s^-1")).test(Quantity.of(2, "km*ms^-1")));
    assertTrue(CompatibleUnitQ.SI().with(Unit.of("PS^2")).test(Quantity.of(2, "W^2")));
    assertFalse(CompatibleUnitQ.SI().with(Unit.of("m*s^-1")).test(Quantity.of(2, "m*s")));
    assertFalse(CompatibleUnitQ.SI().with(Unit.of("s")).test(Quantity.of(1, "Hz")));
  }

  public void testAssignable() {
    assertFalse(Quantity.class.isAssignableFrom(RealScalar.ONE.getClass()));
    assertTrue(Quantity.class.isAssignableFrom(Quantity.of(1, "s").getClass()));
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

  public void testWithFail() {
    AssertFail.of(() -> CompatibleUnitQ.SI().with(null));
  }

  public void testInNullFail() {
    AssertFail.of(() -> CompatibleUnitQ.in(null));
  }
}
