// code by jph
package ch.alpine.tensor.qty;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class DegreeTest extends TestCase {
  private final Unit turns = Unit.of("turns");

  public void testFullRotation() {
    assertEquals(Degree.of(360), Pi.TWO);
  }

  public void testReciprocal() {
    Scalar rad = RealScalar.of(0.2617993877991494);
    Scalar scalar = Degree.of(15).reciprocal();
    assertEquals(scalar.multiply(rad), RealScalar.ONE);
  }

  public void testReciprocal10() {
    Scalar rad = RealScalar.of(0.17453292519943295);
    Scalar scalar = Degree.of(10);
    scalar = scalar.reciprocal();
    assertEquals(scalar.multiply(rad), RealScalar.ONE);
  }

  public void testTurns() {
    assertEquals(UnitSystem.SI().apply(Quantity.of(RationalScalar.HALF, turns)), Pi.VALUE);
    assertEquals(QuantityMagnitude.SI().in(Unit.ONE).apply(Quantity.of(1, turns)), Pi.TWO);
  }

  public void testStringScalarFail() {
    AssertFail.of(() -> Degree.of(StringScalar.of("abc")));
  }
}
