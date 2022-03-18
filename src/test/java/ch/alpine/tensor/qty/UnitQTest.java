// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.usr.AssertFail;

public class UnitQTest {
  @Test
  public void testZero() {
    Unit unit = Unit.of("m^0*s^-0");
    assertTrue(UnitQ.isOne(unit));
  }

  @Test
  public void testDouble() {
    assertEquals(Unit.of("m*m^3"), Unit.of("m*m^2*m"));
    assertTrue(UnitQ.isOne(Unit.of("m*m^-1")));
    assertTrue(UnitQ.isOne(Unit.of("s^2*m*s^-1*m^-1*s^-1")));
  }

  @Test
  public void testEmpty() {
    assertTrue(UnitQ.isOne(Unit.of("")));
    assertTrue(UnitQ.isOne(Unit.ONE));
  }

  @Test
  public void testFail() {
    AssertFail.of(() -> UnitQ.isOne(null));
  }

  @Test
  public void testWeightPercent() {
    Scalar scalar = Quantity.of(2, "kg").divide(Quantity.of(10, "kg"));
    Scalar wtp = UnitConvert.SI().to("mass%").apply(scalar);
    assertEquals(wtp, Quantity.of(20, "mass%"));
    assertFalse(UnitQ.isOne(QuantityUnit.of(wtp)));
  }
}
