// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.num.Pi;

class DegreeTest {
  private final Unit turns = Unit.of("turns");

  @Test
  void testFullRotation() {
    assertEquals(Degree.of(360), Pi.TWO);
  }

  @Test
  void testReciprocal() {
    Scalar rad = RealScalar.of(0.2617993877991494);
    Scalar scalar = Degree.of(15).reciprocal();
    assertEquals(scalar.multiply(rad), RealScalar.ONE);
  }

  @Test
  void testReciprocal10() {
    Scalar rad = RealScalar.of(0.17453292519943295);
    Scalar scalar = Degree.of(10);
    scalar = scalar.reciprocal();
    assertEquals(scalar.multiply(rad), RealScalar.ONE);
  }

  @Test
  void testTurns() {
    assertEquals(UnitSystem.SI().apply(Quantity.of(RationalScalar.HALF, turns)), Pi.VALUE);
    assertEquals(QuantityMagnitude.SI().in(Unit.ONE).apply(Quantity.of(1, turns)), Pi.TWO);
  }

  @Test
  void testStringScalarFail() {
    assertThrows(Throw.class, () -> Degree.of(StringScalar.of("abc")));
  }

  @Test
  void testBytes() {
    assertEquals(QuantityMagnitude.SI().in("B").apply(Quantity.of(3, "MB")), RealScalar.of(3000_000));
    assertEquals(QuantityMagnitude.SI().in("kB").apply(Quantity.of(30_000, "B")), RealScalar.of(30));
    assertEquals(QuantityMagnitude.SI().in("TB").apply(Quantity.of(2, "PB")), RealScalar.of(2000));
  }
}
