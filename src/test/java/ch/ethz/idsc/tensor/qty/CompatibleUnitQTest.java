// code by jph
package ch.ethz.idsc.tensor.qty;

import java.io.IOException;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.ext.Serialization;
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

  public void testWithFail() {
    AssertFail.of(() -> CompatibleUnitQ.SI().with(null));
  }

  public void testInNullFail() {
    AssertFail.of(() -> CompatibleUnitQ.in(null));
  }
}
