// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.mat.Tolerance;

class UnitDimensionsBaseTest {
  @Test
  void testWatt() {
    Scalar scalar = UnitDimensionsBase.SI.plus(Quantity.of(3, "MW"), Quantity.of(100, "kW"));
    assertEquals(scalar, Quantity.of(3100, "kW"));
  }

  @Test
  void testNewton() {
    Scalar scalar = UnitDimensionsBase.SI.plus(Quantity.of(30, "N"), Quantity.of(1, "kg*m*s^-2"));
    assertEquals(scalar, Quantity.of(31, "N"));
  }

  @Test
  void testDegree() {
    Scalar scalar = UnitDimensionsBase.SI.plus(Quantity.of(30, "deg"), RealScalar.ONE);
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(1.5235987755982987));
  }
}
